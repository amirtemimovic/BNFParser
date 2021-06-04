package org.unibl.etf.fmsi.bnfparser;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BNFParser {
    private static final String path = "C:\\Users\\Amir\\Desktop\\faks\\ostalo\\projektni\\IdeaProjects\\BNFParser\\src\\org\\unibl\\etf\\fmsi\\bnfparser\\config.bnf";

    public static void main(String args[]) throws FileNotFoundException
    {
        if (args.length == 0 || args.length == 1) {
            System.err.println("You need to forward the file path!");
            System.exit(1);
        }
        try
        {
            List<String> linesfOfBNFFile = new ArrayList<>();
            BufferedReader bnfReader= new BufferedReader(new FileReader(new File(path)));
            String oneLineBNF;
            while((oneLineBNF = bnfReader.readLine())!=null)
            {
                linesfOfBNFFile.add(oneLineBNF);
            }
            bnfReader.close();
            BNFFileReader bnfFileReader = new BNFFileReader(linesfOfBNFFile.toArray(new String[0]));
            bnfFileReader.read();
            XMLWriter xmlWriter = new XMLWriter(args[1]);
            xmlWriter.write(args[0], bnfFileReader);
            xmlWriter.writeEndDocument();

            for(int i=0; i<bnfFileReader.rules.size(); i++)
            {
                bnfFileReader.rules.get(i).print();
                //bnfFileReader.rules.get(i).printMatched();
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Input text file not found.");
            ex.printStackTrace(System.err);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IndexOutOfBoundsException ex)
        {
            ex.printStackTrace(System.err);
        }
    }

}

