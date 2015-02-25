package com.oldgoat5.ceasarcipher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**********************************************************************
 * @author Michael Evans
 * @version 9/28/2014
 * 
 * Implements a basic Caesar Cipher.  
 ***********************************************************************/
public class MainActivity extends Activity
{
    Button encryptButton;
    Button decryptButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        encryptButton = (Button) findViewById(R.id.encryptTitleButton);
        decryptButton = (Button) findViewById(R.id.decryptTitleButton);
        
        encryptButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showEncryptScreen();
            }
        });
        
        decryptButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDecryptScreen();
            }
        });
    }
    
    /******************************************************************
     * Starts the encryption activity from Encrypt.class.
     ******************************************************************/
    public void showEncryptScreen()
    {
        Intent intent = new Intent(getApplication(), Encrypt.class);
        startActivity(intent);
    }
    
    /******************************************************************
     * Starts the decryption activity from Decrypt.class.
     ******************************************************************/
    public void showDecryptScreen()
    {
        Intent intent = new Intent(getApplication(), Decrypt.class);
        startActivity(intent);
    }
}
