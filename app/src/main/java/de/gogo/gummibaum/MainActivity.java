package de.gogo.gummibaum;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import de.gogo.gummibaum.ui.main.SectionsPagerAdapter;
import de.gogo.gummibaum.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    boolean verbunden = false;

    Socket verbindung;

    static EditText ip;
    static EditText nachricht;
    static TextView chat;
    static Button senden;
    static Button connect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.fragment_main);



        Toast.makeText(getApplicationContext(),"test", Toast.LENGTH_LONG).show();

        ip = (EditText) findViewById(R.id.ip);
        nachricht = (EditText) findViewById(R.id.nachricht);
        chat = (TextView) findViewById(R.id.chat);
        senden = (Button) findViewById(R.id.senden);
        connect = (Button) findViewById(R.id.connect);


        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        connect.setOnClickListener(view -> {
            new Thread() {
                @Override
                public void run() {
                    if (ip.getText().toString().equals("")) {
                        ServerSocket server = null;

                        try {
                            server = new ServerSocket(2330);
                            //Toast.makeText(getApplicationContext(),"wartet auf verbindung", Toast.LENGTH_SHORT).show();
                            verbindung = server.accept();
                            //Toast.makeText(getApplicationContext(),"verbindung aufgebaut", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            //Toast.makeText(getApplicationContext(),"verbindung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        //Toast.makeText(getApplicationContext(),"verbindet...", Toast.LENGTH_SHORT).show();
                        try {
                            verbindung = new Socket(ip.getText().toString(),2330);
                            //Toast.makeText(getApplicationContext(),"verbunden", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            //Toast.makeText(getApplicationContext(),"verbindung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }.start();
        });

        senden.setOnClickListener(view -> {
            new Thread(){
                @Override
                public void run() {
                    DataOutputStream dos = null;
                    try {
                        dos = new DataOutputStream(verbindung.getOutputStream());
                        dos.writeUTF(nachricht.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        });

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (verbunden) {
                        try {
                            DataInputStream dis = new DataInputStream(verbindung.getInputStream());
                            String empfangen = new String(dis.readUTF());
                            chat.setText(empfangen);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }
}