package com.oldgoat5.ceasarcipher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**********************************************************************
 * This class contains the activity for decrypting a message.  A valid 
 * message string can only contain uppercase characters and no whitespace.  
 * A shift number can be an integer from 0 - 25.
 * 
 * The message and shift number can be typed into the program 
 * ((R.id.inputDecryptEditText), (R.id.shiftNumberEditText)),
 * or read from a text file (R.id.fileSelectButton). 
 * 
 * A valid text file will have the message as the first line, and the 
 * shift number as the second line.  
 *  
 * The output will be displayed in a scrollable text view 
 * (R.id.outputTextView).  It can also be saved to a file 
 * (/storage/extSDCard/output_decrypt.txt).
 **********************************************************************/
public class Decrypt extends Activity
{
    private static final int CONST_RESULT = 1;
    
    Button decryptButton;
    Button outputTextButton;
    Button selectButton;
    EditText inputDecryptEditText;
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
        setContentView(R.layout.decrypt_layout);
        
        decryptButton = (Button) findViewById(R.id.decryptTextButton);
        outputTextButton = (Button) findViewById(R.id.outputTextButton);
        selectButton = (Button) findViewById(R.id.fileSelectButton);
        inputDecryptEditText = (EditText) findViewById(R.id.inputDecryptEditText);
        shiftNumberEditText = (EditText) findViewById(R.id.shiftNumberEditText);
        fileText = (TextView) findViewById(R.id.fileDecryptTextView);
        outputTextView = (TextView) findViewById(R.id.outputTextView);
        outputSavedTextView = (TextView) findViewById(R.id.outputSavedTextView);
        
        outputSavedTextView.setVisibility(View.INVISIBLE);
        outputTextView.setText(outputText);
        
        //decrypt button listen
        decryptButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startDecryption();   
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
                File output;
                PrintWriter writer;
                
                output = new File("/storage/extSdCard/output_decrypt.txt");
                
                try
                {
                    writer = new PrintWriter(output);
                    writer.write(outputTextView.getText().toString());
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
     * the text view (R.id.fileDecryptTextView).
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
     * Decrypts a message from a file if a file has been 
     * specified (R.id.fileText).  If no file has been specified, the 
     * input edit text view (R.id.inputDecryptEditText) is used as the 
     * message.
     * 
     * @throws FileNotFoundException if the input file is not found.
     * 
     * @throws NumberFormatException if no shift number was entered.  
     ******************************************************************/
    private void startDecryption()
    {
        int shiftNumber;
        String inputMessage;
        
        Log.d("startDecryption()", fileText.getText().toString());
        
        //if filepath is null, use the text fields as input
        if (fileText.getText().toString() == null || 
            fileText.getText().toString().equals("(No File Selected)"))
        {
            Log.d("startDecryption()", "if filetext == null called, no file");
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
                inputMessage = inputDecryptEditText.getText().toString();
                //then encrypt
                decryptMessage(inputMessage, shiftNumber);
            }
            catch (NumberFormatException e)
            {
                outputText += "\nPlease input a shift number from 0 - 25.";
                outputTextView.setText(outputText);
            }
        }
        else
        {
            Log.d("startDecryption()", "else called");
            //clear editTexts
            inputDecryptEditText.setText("");
            shiftNumberEditText.setText("");
            //parse the .txt to retrieve data
            try
            {
                Log.d("startDecryption()", "else: try called");
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
                    decryptMessage(inputMessage, shiftNumber);
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
     * Decrypts the given message to the given shift number.  The 
     * decrypted result is returned without whitespace and as a string
     * of uppercase characters.  Adds the result to the output text 
     * view (R.id.outputTextView).
     * 
     * @param message - The message to decrypt.
     * 
     * @param shiftNumber - The number to shift by.
     ********************************************************************/
    private void decryptMessage(String message, int shiftNumber)
    {
        ArrayList<Character> messageCharList;
        char[] messageCharArray;
        int[] messageIntArray;
        String decryptedMessage = "";
        
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
            //invert shiftNumber
            shiftNumber = -shiftNumber;
            //just remove whitespace from message...
            message = message.replaceAll("\\s+", "");
            Log.d("message", message);
            Log.d("message.length", Integer.toString(message.length()));
            messageCharList = new ArrayList<Character>();
            //create charlist  
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
            //create decrypted charlist
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
                Log.d("() % 26", Integer.toString((messageCharList.get(i) + shiftNumber) % 26));
                Log.d("messageIntArray[i]", Integer.toString(messageIntArray[i]));
                Log.d("CharArray[i]", Character.toString(messageCharArray[i]));
            }
            //create decrypted message
            for (int i = 0; i < messageCharArray.length; i++)
            {
                decryptedMessage += messageCharArray[i];
            }
        }
        Log.d("encryptMessage(): return", decryptedMessage);
        outputText += "\n" + decryptedMessage;
        outputTextView.setText(outputText);
    }
    
    /******************************************************************
     * Checks whether the given message is valid.  A valid message 
     * contains all uppercase characters and no whitespace.  
     * Stores invalid characters and their positions in the string 
     * in a hashmap (badChars).
     * 
     * @param message - The message to check for validity.
     * 
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
            if (!(message.charAt(i) >= 65 && message.charAt(i) <= 90))
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
