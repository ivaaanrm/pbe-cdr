const Http = new XMLHttpRequest();

async function httprequestlogin() {
    var usuario = document.getElementById("username-field").value;
    var password = document.getElementById("password-field").value; 
    sessionStorage.setItem("user", usuario);
    sessionStorage.setItem("id", password);

    res = Http.open("GET", 'http://localhost:3000/' + password + '?username=' + usuario);
    console.log('http://localhost:3000/' + password + '?username=' + usuario)
    Http.responseType = 'json';
    Http.send();

    Http.onreadystatechange = (e) => {
        console.log(Http.status)
        if(Http.readyState == XMLHttpRequest.DONE && Http.status != 200){
            alert("Incorrect username or password.")
        }
        else if(Http.readyState == XMLHttpRequest.DONE && Http.status == 200){
            window.location.href = "src/pages/queryPage.html";
        }

    }
}

