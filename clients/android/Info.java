package com.example.iwell;

import android.content.Context;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Info extends AppCompatActivity {
    public final TableLayout table_layout;
    public final Context context;
    public String[] headers;
    public ArrayList<String[]> data;
    public TableRow table_row;
    public TextView cell_text;
    public int col, row, color, color2, color_text;
    public boolean m_color;

    public Info(TableLayout table_layout, Context context) {
        this.table_layout = table_layout;
        this.table_layout.removeAllViewsInLayout();
        this.context = context;
        this.m_color = false;
    }

    public void put_header(String[] header) {
        this.headers = header;
        this.header();
    }

    public void put_data(ArrayList<String[]> data) {
        this.data = data;
        this.create_table(data.size());
    }

    public void new_row() {
        this.table_row = new TableRow(context);
    }

    public void cell() {
        this.cell_text = new TextView(context);
        this.cell_text.setTextSize(17);
        this.cell_text.setGravity(Gravity.CENTER);
    }

    public void header() {
        this.new_row();
        for (this.col = 0; this.col < this.headers.length; this.col++) {
            this.cell();
            this.cell_text.setText(this.headers[this.col]);
            this.table_row.addView(this.cell_text, TableRow_LayoutParams());
        }
        this.table_layout.addView(table_row);
    }

    public void create_table(int length) {
        for (row = 1; row <= length; row++) {
            this.new_row();
            for (col = 0; col < headers.length; col++) {
                this.cell();
                String[] row = this.data.get(this.row - 1);
                String str = (col < row.length) ? row[col] : "";
                this.cell_text.setText(str);
                this.table_row.addView(cell_text, TableRow_LayoutParams());
            }
            this.table_layout.addView(table_row);
        }
    }

    public void header_background(int color) {
        this.col = 0;
        this.new_row();
        while (col < headers.length) {
            this.cell_text = get_cell(0, col++);
            this.cell_text.setBackgroundColor(color);
        }
    }

    public void data_background(int first_color, int second_color, int size) {
        for (row = 1; row <= size; row++) {
            this.m_color = !m_color;
            for (col = 0; col < headers.length; col++) {
                this.cell_text = get_cell(row, col);
                this.cell_text.setBackgroundColor((m_color) ? first_color : second_color);
            }
        }
        this.color = first_color;
        this.color2 = second_color;
    }

    public void data_color(int color, int size) {
        for (row = 1; row <= size; row++)
            for (col = 0; col < headers.length; col++)
                this.get_cell(row, col).setTextColor(color);
        this.color_text = color;
    }

    public void header_color(int color) {
        this.col = 0;
        while (col < headers.length)
            this.get_cell(0, col++).setTextColor(color);
    }

    public void color_line(int color) {
        this.row = 0;
        while (row <= data.size()) {
            get_row(row++).setBackgroundColor(color);
        }
    }

    public TableRow get_row(int index) {
        return (TableRow) this.table_layout.getChildAt(index);
    }

    public TextView get_cell(int rowIndex, int columIndex) {
        this.table_row = get_row(rowIndex);
        return (TextView) this.table_row.getChildAt(columIndex);
    }

    @NonNull
    public TableRow.LayoutParams TableRow_LayoutParams() {
        TableRow.LayoutParams p = new TableRow.LayoutParams();
        p.setMargins(5, 5, 5, 5);
        p.weight = 1;
        return p;
    }
}

