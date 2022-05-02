import gi
gi.require_version('Gtk','3.0')
from gi.repository import Gtk, Gdk, GLib
import requests
from requests.exceptions import *
from threading import Timer, Thread
from src.rfid import Rfid
from src.lcdDrivers.lcddriver import Lcd 


class MyApplication(Gtk.Window):
    def __init__(self):
        super().__init__(title="Course manager")                                 # Call to the constructor

        self.DOMAIN = 'http://XXX.XXX.X.XXX:XXXX'                                # The domain of the server
        self.SESSION_TIME = 300                                                  # The seconds before session expires

        self.set_default_size(900, 600)                                          # Set the size of the window
        self.set_position(Gtk.WindowPosition.CENTER)                             # Set the position of the window
        self.set_border_width(50)                                                # Set some space for inner widgets

        self.nfc_reader = Rfid()                                                 # Create the Rfid object
        self.display = Lcd()                                                     # Create the Lcd object

        self.login_window()                                                      # Create the login window
        self.read_uid_thread = Thread(target=self.read_uid, daemon=True)         # Create a thread
        self.read_uid_thread.start()                                             # Start the thread
        
    # Display login window function  
    def login_window(self): 
        self.box = Gtk.Box(orientation=Gtk.Orientation.VERTICAL, spacing=10)     # Create the box widget
        self.add(self.box)                                                       # Add the box widget to the window
        self.set_border_width(50)                                                # Set some space for inner widgets

        self.label = Gtk.Label()                                                 # Create the welcome label
        self.label.set_name("first-label")                                       # Set an ID to the label
        self.label.set_text("Please, login with your university card")           # Set the inital text to the label
        self.label.set_size_request(400,75)                                      # Set the minimum size of the widget
        self.box.pack_start(self.label, True, False, 0)                          # Add the label to the box
    
    # Timeout function
    def start_timer(self):
        self.timer = Timer(self.SESSION_TIME, self.logout)                       # Create a timer that logout on expire
        self.timer.setDaemon(True)                                               # Set Daemon mode so the timer can die
        self.timer.start()                                                       # Start timer
    
    # Correct login function
    def correct_login(self):
        self.display.lcd_display_string("Welcome", 1)                            # Display the welcome msg on the lcd
        self.display.lcd_display_string(self.username, 3)                        # Display the user on the lcd
        self.remove(self.box)                                                    # Remove the login box
        self.dashboard_window()                                                  # Create the dashboard 
        self.show_all()                                                          # Show all widgets

    # Read UID function
    def read_uid(self):
        self.uid = self.nfc_reader.read_uid()                                    # Set the UID value
        try:
            self.username = self.make_login(self.uid)                            # Get user form UID
            GLib.idle_add(self.correct_login)                                    # Correct read of the UID
        except (ConnectionError):                                                # Connection error exception
            GLib.idle_add(self.dialog_box, "SERVICE UNAVAILABLE", 
                          "Unable to connect: server is not up.")                # Open dialog box with error

    # Get username from UID function    
    def make_login(self,id):
        r = requests.get(self.DOMAIN+'/'+id)                                     # Create a request for the username
        r.raise_for_status()                                                     # Send the request
        return r.json()["username"]                                              # Return the username

    # Create the after login page function
    def dashboard_window(self):
        self.start_timer()                                                       # Start the timer for logout
        self.box = Gtk.Grid(column_homogeneous=True)                             # Create the box widget
        self.box.set_name("dashboard-box")                                       # Set an ID to the box
        self.box.set_column_spacing(10)                                          # Set padding between widgets in box
        self.box.set_row_spacing(15)                                             # Set padding between widgets in box
        self.add(self.box)                                                       # Add the box widget to the window        
        
        welcome_label = Gtk.Label()                                              # Create the welcome label
        welcome_label.set_name("welcome-label")                                  # Set an ID to the label
        welcome_label.set_text("Welcome %s" % self.username)                     # Set the text to the label
        self.box.attach(welcome_label,0,0,2,1)                                   # Add the label to the box

        logout = Gtk.Button()                                                    # Create the logout button widget
        logout.set_name("logout")                                                # Set an ID to the button
        logout.set_label("logout")                                               # Set te label of te button
        logout.connect("clicked", self.logout)                                   # Connect the button to the logout function
        self.box.attach(logout,5,0,1,1)                                          # Add button to the box

        entry = Gtk.SearchEntry()                                                # Create the search bar widget
        entry.set_placeholder_text("Enter your query")                           # Set a placehholder to the entry
        entry.connect("activate", self.entry_get_data)                           # Connect the widget to the query function 
        self.box.attach(entry,0,1,6,1)                                           # Add widget to the box


        self.scrollable = Gtk.Label()
        self.scrollable.set_name("tips-label")                                   # Set an ID to the label
        self.scrollable.set_text('you can try "marks"')                          # Set the inital text to the label
        self.box.attach(self.scrollable, 0, 2, 6, 25)                            # Add the label to the box
        
    # Search by query function
    def entry_get_data(self, entry):
        self.timer.cancel()                                                      # Cancel the logout timer
        self.start_timer()                                                       # Start the timer for logout                                            
        self.get_data_thread = Thread(target=self.get_data, daemon=True, 
                                      args=(entry.get_text(),))                  # Create a thread
        self.get_data_thread.start()                                             # Start the thread

    # Get data from query function
    def get_data(self, query):
        try: 
            req = requests.get(self.DOMAIN+'/'+self.uid+'/'+query)               # Create a request for the query
            req.raise_for_status()                                               # Send the request
            self.info = req.json()                                               # Save the data from thhe request
            GLib.idle_add(self.info_window)                                      # Call the diplay info function
        except HTTPError as e:
            GLib.idle_add(self.dialog_box, e.response.reason, 
                          'query "'+ query +'" not valid')                       # Connection error exception query not valid
        except (ConnectionError):                                                # Connection error exception
            GLib.idle_add(self.dialog_box, "SERVICE UNAVAILABLE", 
                          "Unable to connect: server is not up.")                # Open dialog box with error

    # Display info function        
    def info_window(self):
        self.box.remove(self.scrollable)                                         # Remove the previous info box
        self.scrollable = Gtk.ScrolledWindow()                                   # Create an scrollable window
        self.scrollable.set_name("scrollable")                                   # Set an ID to the label
        self.box.attach(self.scrollable, 0, 2, 6, 25)                            # Add the scrollable window to the box
        if len(self.info) > 0:                                                   # Check if we have info to display
            self.treeview = TreeView(self.info)                                  # Create a treeview widget with the info
            self.treeview.columns_autosize()                                     # Set the size of the columns 
            self.treeview.set_grid_lines(True)
            self.scrollable.add(self.treeview)                                   # Add the treeview to the scrollable widget
        else:                                                                    
            self.dialog_box("NOT FOUND", "No data matches your query.")          # Send error msg if no data
        self.show_all()                                                          # Show all widgets

    # logout function        
    def logout(self,button = None):
        self.display.lcd_clear()                                                 # Clear the lcd
        self.remove(self.box)                                                    # Remove the dashboard box
        self.timer.cancel()                                                      # Cancel the logout timer
        self.login_window()                                                      # Display the login window
        self.read_uid_thread = Thread(target=self.read_uid, daemon=True)         # Create a thread
        self.read_uid_thread.start()                                             # Start the thread
        self.show_all()                                                          # Show all widgets

    # Error dialog function
    def dialog_box(self, reason, text):
        self.dialog = Gtk.MessageDialog(parent = self)                           # Create te dialog widget
        self.dialog.add_button("_Close", Gtk.ResponseType.CLOSE)                 # Connect the dialog window to the close button
        self.dialog.set_name("dialog-box")                                       # Set an ID to the dialog box
        self.dialog.set_border_width(20)                                         # Set some space for inner widgets
        self.dialog.set_decorated(False)                                         # Remove headerbar
        self.dialog.set_markup("<span><b>%s</b></span>" % reason.upper())        # Set the text to the dialog box
        self.dialog.set_default_size(500, 100)                                    # Set the size of the dilog window
        self.dialog.format_secondary_text(text)                                  # Set the style of the body text
        self.dialog.run()                                                        # Show the dialog window
        self.dialog.hide()                                                       # Connect the close button of the dialog window


class TreeView(Gtk.TreeView):
    def __init__(self, info):
        self.model = Gtk.ListStore.new([type(e) for e in info[0].values()])      # Create a list of all the elements on info
        for elem in info:
            self.model.append(elem.values())                                     # Append the elements of the list

        super().__init__(model = self.model)                                     # Call to the constructor
        
        for i, key in enumerate(info[0].keys()):
            renderer = Gtk.CellRendererText(single_paragraph_mode=True)          # Render text in its cell with it's custom style
            renderer.set_alignment(0.5, 0.5)                                     # Set the alignment
            col = Gtk.TreeViewColumn(key, renderer, text=i)                      # Create a column
            col.set_expand(True)                                                 # Set the column to take available extra space
            col.set_alignment(0.5)                                               # Set the column alignment
            self.append_column(col)                                              # Add thhe column



if __name__ == "__main__":
    style_provider = Gtk.CssProvider()                                           # Create a provider where we will get the style
    style_provider.load_from_path('src/style.css')                                   # Set the path to the css file
    Gtk.StyleContext.add_provider_for_screen(
        Gdk.Screen.get_default(), style_provider,
        Gtk.STYLE_PROVIDER_PRIORITY_APPLICATION
    )                                                                            
    win = MyApplication()                                                        # Open the application calling it's function
    win.connect("destroy", Gtk.main_quit)                                        # Connect the window to the close button
    win.show_all()                                                               # Show all widgets created on the windows object
    Gtk.main()                                                                   # Create a loop for the scipt to run