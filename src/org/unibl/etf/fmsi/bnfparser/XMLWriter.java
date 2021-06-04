package org.unibl.etf.fmsi.bnfparser;

import javax.sound.midi.SoundbankResource;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLWriter
{
    private String fileName;
    FileOutputStream fileOutputStream;
    XMLOutputFactory xmlOutputFactory;
    XMLStreamWriter xmlStreamWriter;

    int lineCounter = 0;

    public XMLWriter(String fileName)
    {
        try
        {
            this.fileName = fileName;
            fileOutputStream = new FileOutputStream(fileName);
            xmlOutputFactory = XMLOutputFactory.newInstance();
            xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(fileOutputStream, "UTF-8");
            xmlStreamWriter.writeStartDocument("utf-8", "1.0");
            xmlStreamWriter.writeStartElement("BNF_Parser");
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace(System.err);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace(System.err);
        }
    }

    private boolean isAlone(String s)
    {
        String regex = "<[a-z_]+>";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()){
            return false;
        }
        return true;
    }

    public void writeEndDocument()
    {
        try
        {
            xmlStreamWriter.writeEndDocument();
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace(System.err);
        }
    }

    public void write(String inputFileName, BNFFileReader bnfFileReader)
    {
        try
        {
            String regex = "(" + bnfFileReader.rules.get(0).getRegex() + ")";
            String oneLine;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(inputFileName)));
            int counter = 0, matchCounter = 0;
            while ((oneLine = bufferedReader.readLine())!= null)
            {
                counter++;
                Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(oneLine);
                while (matcher.find())
                {
                    LinkedList<String> matches = new LinkedList<>();
                    for(int i=0; i<bnfFileReader.rules.size(); i++)
                    {
                        matches.add(i, new String());
                    }
                    String reserveRegex = "(" + bnfFileReader.rules.get(0).getRegex() + " ?)+";
                    Pattern reservePattern = Pattern.compile(reserveRegex, Pattern.MULTILINE);
                    Matcher reserveMatcher = reservePattern.matcher(oneLine);
                    if(reserveMatcher.find())
                    {
                        ++matchCounter;
                        if(!(reserveMatcher.group(0).contains(oneLine)))
                        {
                            System.out.println("THE INPUT FILE CANNOT BE PARSED BY GIVEN BNF FORM!");
                            System.out.println(matchCounter + "  " + counter);
                            System.exit(1);
                        }
                    }
                    xmlStreamWriter.writeStartElement(bnfFileReader.rules.get(0).getToken());
                    matches.set(0, matcher.group(0));
                    lineCounter++;

                    for(int i=0; i<bnfFileReader.rules.size(); i++)
                    {
                        String  tr= "<([a-z_]+)>";
                        Pattern tPattern = Pattern.compile(tr, Pattern.MULTILINE);
                        Matcher tMatcher = tPattern.matcher(bnfFileReader.rules.get(i).getDefinition());

                        while (tMatcher.find())
                        {
                            for(int j=0; j<bnfFileReader.rules.size(); j++)
                            {
                                if(bnfFileReader.rules.get(j).getToken().equals(tMatcher.group(1)))
                                {
                                    search(matches, j, bnfFileReader, i);
                                    break;
                                }
                            }
                        }
                    }
                }
                System.out.println(matchCounter + "   " + counter);
                if(matchCounter!=counter)
                {
                    System.out.println("THE INPUT TEXT FILE CANNOT BE PARSED BY GIVEN BNF FORM.");
                    System.exit(1);
                }

                for(int i=0; i<lineCounter; i++)
                {
                    xmlStreamWriter.writeEndElement();
                }

            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace(System.err);
        }
        catch (IOException ex)
        {
            ex.printStackTrace(System.err);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace(System.err);
        }
    }

    private void search(List<String> matches, int tIndex, BNFFileReader bnfFileReader, int index)
    {
        try {
            if (isAlone(bnfFileReader.rules.get(tIndex).definition)) {
                String lTokenRegex = bnfFileReader.rules.get(tIndex).regex;
                Pattern lTokenPattern = Pattern.compile(lTokenRegex, Pattern.MULTILINE);
                Matcher lTokenMatcher = lTokenPattern.matcher(matches.get(index));
                if (lTokenMatcher.find())
                {
                    xmlStreamWriter.writeStartElement(bnfFileReader.rules.get(tIndex).token);
                    xmlStreamWriter.writeCharacters(lTokenMatcher.group(0));
                    xmlStreamWriter.writeEndElement();

                    String tmp = matches.get(index).replace(lTokenMatcher.group(0), "");
                    matches.set(index, tmp);

                    return;
                }
            } else {
                final String tokenRegex = "<([a-z_]+)>";
                final Pattern tokenPattern = Pattern.compile(tokenRegex, Pattern.MULTILINE);
                final Matcher tokenMatcher = tokenPattern.matcher(bnfFileReader.rules.get(tIndex).definition);

                xmlStreamWriter.writeStartElement(bnfFileReader.rules.get(tIndex).token);


                while (tokenMatcher.find()) {
                    for (int i = 0; i < bnfFileReader.rules.size(); i++) {
                        if (bnfFileReader.rules.get(i).token.equals(tokenMatcher.group(1))) {
                            search(matches, i, bnfFileReader, index);
                            break;
                        }
                    }
                }
                xmlStreamWriter.writeEndElement();
            }
            return;
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace(System.err);
        }
    }
}
