package com.ads.jobsjop20;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.entrarButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para a tela de login
                Intent intent = new Intent(MainActivity.this, atvLogin.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.cadastroButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para a tela de cadastro
                Intent intent = new Intent(MainActivity.this, atvCadastro.class);
                startActivity(intent);
            }
        });
    }
}
