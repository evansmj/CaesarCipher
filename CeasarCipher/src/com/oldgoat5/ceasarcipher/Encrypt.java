package com.oldgoat5.ceasarcipher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**********************************************************************
 * This class contains the activity for encrypting a message.  A valid 
 * message string can only contain uppercase, lowercase, and whitespace 
 * characters.  A shift number can be an integer from 0 - 25.
 * 
 * The message and shift number can be typed into the program 
 * ((R.id.inputEncryptEditText), (R.id.shiftNumberEditText)),
 * or read from a text file (R.id.fileSelectButton). 
 * 
 * A valid text file will have the message as the first line, and the 
 * shift number as the second line.  
 *  
 * The output will be displayed in a scrollable text view 
 * (R.id.outputTextView).  It can also be saved to a file 
 * (/storage/extSDCard/output_encrypt.txt).
 **********************************************************************/
public class Encrypt extends Activity
{
    private static final int CONST_RESULT = 1;
    
    Button encryptButton;
    Button outputTextButton;
    Button selectButton;
    EditText inputEncryptEditText;
    EditText shiftNumberEditText;
    TextView fileText;
    TextView outputTextView;
    TextView outputSavedTextView;
    
    HashMap<Integer, Character> badChars;
    String filePath = "";
    String outputText = "Output: \n";
    
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.encrypt_layout);
        
        encryptButton = (Button) findViewById(R.id.encryptTextButton);
        outputTextButton = (Button) findViewById(R.id.outputTextButton);
        selectButton = (Button) findViewById(R.id.fileSelectButton);
        inputEncryptEditText = (EditText) findViewById(R.id.inputEncryptEditText);
        shiftNumberEditText = (EditText) findViewById(R.id.shiftNumberEditText);
        fileText = (TextView) findViewById(R.id.fileEncryptTextView);
        outputTextView = (TextView) findViewById(R.id.outputTextView);
        outputSavedTextView = (TextView) findViewById(R.id.outputSavedTextView);
        
        outputSavedTextView.setVisibility(View.INVISIBLE);
        outputTextView.setText(outputText);
        
        //encrypt button listen
        encryptButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startEncryption();   
            }
        });
        
        //file select button method
        selectButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, CONST_RESULT);
            }
        });
       
        //output to text file method
        outputTextButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                File outputFile;
                PrintWriter writer;
                
                outputFile = new File(Environment.getExternalStorageDirectory()
                        .getAbsoluteFile(), "output_encrypt.txt");
                try
                {
                    writer = new PrintWriter(outputFile);
                    writer.write(outputTextView.getText().toString());
                    //TODO 
                    //fix it not writing
                    //report new location, due to google.
                    
                    Log.d("outputTextButton", "inside try after write");
                    Log.d("Environmet.getExtDir.getAbsFile()", Environment.getExternalStorageDirectory()
                        .getAbsoluteFile().toString());
                }
                catch (FileNotFoundException e)
                {
                    outputText += "\n" + e.getMessage();
                    outputTextView.setText(outputText);
                }
            }
        });
    }
    
    /******************************************************************
     * If an input file location is returned by the file browser
     * activity, set the given file path and display the file path in 
     * the text view (R.id.fileEncryptTextView).
     * 
     * @param requestCode - The integer request code originally supplied
     *  to startActivityForResult(), allowing you to identify who this 
     *  result came from.
     *  
     * @param resultCode - The integer result code returned by the 
     *  child activity through its setResult().
     *  
     * @param data - An Intent, which can return result data to the 
     *  caller (various data can be attached to Intent "extras").
     ******************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
        Intent data)
    {
        switch (requestCode)
        {
            case CONST_RESULT:
            {
                if (resultCode == RESULT_OK)
                {
                    filePath = data.getData().getPath();
                    fileText.setText(filePath);
                }
                break;
            }
        }
    }
    
    /******************************************************************
     * Encrypts a message from a file if a file has been 
     * specified (R.id.fileText).  If no file has been specified, the 
     * input edit text view (R.id.inputEcryptEditText) is used as the 
     * message.
     * 
     * @throws FileNotFoundException if the input file is not found.
     * @throws NumberFormatException if no shift number was entered.  
     ******************************************************************/
    private void startEncryption()
    {
        int shiftNumber;
        String inputMessage;
        
        Log.d("startEncryption()", fileText.getText().toString());
        
        //if filepath is null, use the text fields as input
        if (fileText.getText().toString() == null || 
            fileText.getText().toString().equals("(No File Selected)"))
        {
            Log.d("startEncryption()", "if filetext == null called, no file");
            //use text fields for data
            try
            {
                if (shiftNumberEditText.getText().toString().contains("+"))
                {
                    String tempText = shiftNumberEditText.getText().toString();
                    tempText = tempText.substring(1);
                    shiftNumberEditText.setText(tempText);
                }
                if (shiftNumberEditText.getText().toString() != "")
                {
                    shiftNumber = Integer.parseInt(
                        shiftNumberEditText.getText().toString());
                }
                else
                {
                    shiftNumber = 0;
                }
                inputMessage = inputEncryptEditText.getText().toString();
                //then encrypt
                encryptMessage(inputMessage, shiftNumber);
            }
            catch (NumberFormatException e)
            {
                outputText += "\nPlease input a shift number from 0 - 25.";
                outputTextView.setText(outputText);
            }
        }
        else
        {
            //clear the editTexts
            inputEncryptEditText.setText("");
            shiftNumberEditText.setText("");
            Log.d("startEncryption()", "else called");
            //parse the .txt to retrieve data
            try
            {
                Log.d("startEncryption()", "else: try called");
                try
                {
                    File file = new File(filePath);
                    Scanner scanner = new Scanner(file);
                    String shiftText = "";
                    //get first line - message
                    if (scanner.hasNextLine())
                    {
                        inputMessage = scanner.nextLine();
                    }
                    else
                    {
                        inputMessage = "";
                    }
                    //get 2nd line - shift number
                    if (scanner.hasNextLine())
                    {
                        shiftText = scanner.nextLine();
                        if (shiftText.charAt(0) == '+')
                        {
                            shiftText = shiftText.substring(1);
                        }
                        shiftNumber = Integer.parseInt(shiftText);
                    }
                    else
                    {
                        shiftText = "";
                        shiftNumber = 0;
                    }
                    // if it worked then encrypt
                    scanner.close();
                    encryptMessage(inputMessage, shiftNumber);
                }
                catch (NumberFormatException e)
                {
                    outputText += "\nPlease input a shift number from 0 - 25.";
                    outputTextView.setText(outputText);
                }
            }
            catch (FileNotFoundException e)
            {
                outputText = outputTextView.getText().toString();
                outputText += "\n" + "The file at " + filePath + 
                    " was not found.";
                outputTextView.setText(outputText);
            }
        }
    }
    
    /******************************************************************
     * Encrypts the given message to the given shift number.  The 
     * encrypted result is stripped of whitespace and is set to 
     * uppercase letters.  Adds the result to the output text 
     * view (R.id.outputTextView).
     * 
     * @param message - The message to encrypt.
     * @param shiftNumber - The number to shift by.
     ********************************************************************/
    private void encryptMessage(String message, int shiftNumber)
    {
        ArrayList<Character> messageCharList;
        char[] messageCharArray;
        int[] messageIntArray;
        String encryptedMessage = "";
        
        if (message != "")
        {
            if (!isValidMessage(message))
            {
                outputText = outputTextView.getText().toString();
                //message invalid give badChars
                outputText += "\n" + "The message \"" + message + "\" contains "
                    + "invalid character(s): " + badChars.values() + 
                    " at position(s): " + badChars.keySet().toString();
                outputTextView.setText(outputText);
            }

            if (!isValidShiftNumber(shiftNumber))
            {
                outputText = outputTextView.getText().toString();
                //number invalid
                //give badChars
                outputText += "\n" + "The shift number " + shiftNumber + 
                    " must be " + "no greater than |25|.";
                outputTextView.setText(outputText);
            }
        }
        else
        {
            outputText = outputTextView.getText().toString();
            outputText += "\n" + "Nothing to Encrypt...";
            outputTextView.setText(outputText);
        }
        
        //continue
        if (isValidMessage(message) && isValidShiftNumber(shiftNumber))
        {
            Log.d("encryptMessage", "//continue");
            message = message.replaceAll("\\s+", "");
            Log.d("message", message);
            Log.d("message.length", Integer.toString(message.length()));
            messageCharList = new ArrayList<Character>();
            //create character list  
            for (int i = 0; i < message.length(); i++)
            {
                //create lowercase
                if (message.charAt(i) >= 97 && message.charAt(i) <= 122)
                {
                    messageCharList.add((char) (message.charAt(i) - 32));
                }
                //convert uppercase to lowercase
                if (message.charAt(i) >= 65 && message.charAt(i) <= 90)
                {
                    messageCharList.add((char) (message.charAt(i)));
                }
            } 
            Log.d("arrayList", messageCharList.toString());
            messageCharArray = new char[messageCharList.size()];
            messageIntArray = new int[messageCharList.size()];
            for (int i = 0; i < messageCharList.size(); i++)
            {
                //reduce to 0-25 ints
                messageIntArray[i] = messageCharList.get(i) - 65;
                Log.d("messageIntArray[i]", Integer.toString(messageIntArray[i]));
            }
            //create encrypted charlist.  Correct negative moduluo.  
            for (int i = 0; i < messageCharList.size(); i++)
            {
                int temp = 0;
                
                temp = (messageIntArray[i] + shiftNumber) % 26;
                if (temp < 0)
                {
                    temp += 26;
                }
                messageCharArray[i] = (char) (temp + 65); 
                
                Log.d("shift number", Integer.toString(shiftNumber));
                Log.d("messageCharList.get(i)", messageCharList.get(i).toString());
                Log.d("messageIntArray[i] + shiftnumber", Integer.toString(messageIntArray[i] + shiftNumber));
                Log.d("() % 26", Integer.toString(((messageIntArray[i]) + shiftNumber) % 26));
                Log.d("messageIntArray[i]", Integer.toString(messageIntArray[i]));
                Log.d("CharArray[i]", Character.toString(messageCharArray[i]));
            }
            /*Log.d("test modulus 7 mod 26 = ", Integer.toString(7 % 26));
            int test;
            test = -7 % 26;
            if (test < 0)
                test += 26;
            Log.d("test modulus -7 mod 26", Integer.toString(test));*/
            //create encrypted message
            for (int i = 0; i < messageCharArray.length; i++)
            {
                encryptedMessage += messageCharArray[i];
            }
        }
        Log.d("encryptMessage(): return", encryptedMessage);
        outputText += "\n" + encryptedMessage;
        outputTextView.setText(outputText);
    }
    
    /******************************************************************
     * Checks whether the given message is valid.  Valid characters are
     * capital and lowercase letters, and whitespace.  Stores invalid
     * characters and their positions in the string in a hashmap 
     * (badChars).
     * 
     * @param message - The message to check for validity.
     * @return Returns true if the message contains no invalid 
     *  characters, false otherwise.  
     ******************************************************************/
    @SuppressLint("UseSparseArrays")
    private boolean isValidMessage(String message)
    {
        boolean result;
        
        badChars = new HashMap<Integer, Character>();
        result = true;
        
        //true until proven otherwise
        for (int i = 0; i < message.length(); i++)
        {
            if (!(((message.charAt(i) >= 65 && message.charAt(i) <= 90) ||
                (message.charAt(i) >= 97 && message.charAt(i) <= 122)) || 
                message.charAt(i) == 32))
            {
                Log.d("verifyMessage()", "else called");
                Log.d("verifyMessage(): else", Character.toString(message.charAt(i)));
                //tell user which chars are bad
                badChars.put(i, message.charAt(i));
                result = false;
            }
        }
        return result;
    }
    
    /******************************************************************
     * Checks whether the given shift number is valid or not.  A 
     * valid shift number is an integer between 0 and 25.  
     * 
     * @param shiftNumber - The number to check.
     * 
     * @return Returns true if the number is a valid shift number, false
     *  otherwise.  
     ******************************************************************/
    private boolean isValidShiftNumber(int shiftNumber)
    {
        boolean result;
        
        result = false;
        Log.d("verifyShiftNumber()", Integer.toString(shiftNumber));
        if (shiftNumber > 25 || shiftNumber < -25)
        {
            result = false;
        }
        else
        {
            result = true;
        }
        return result;
    }
}
