package org.unibl.etf.fmsi.bnfparser;

/*import javax.sound.midi.SoundbankResource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;*/

public class BNFRules
{
    public String token;
    public String definition;
    public String regex;
    //public List<Node> parts = new ArrayList<>();
    //public List<String> matchedLines = new ArrayList<>();

    public BNFRules(String token, String definition, String regex)
    {
        this.token = token;
        this.definition = definition;
        this.regex = regex;
        //extractParts(definition);
    }

    public String getToken()
    {
        return token;
    }

    public  String getDefinition()
    {
        return definition;
    }

    public String getRegex()
    {
        return regex;
    }

    public void print()
    {
        System.out.println(token + " ::= " + definition);
        System.out.println("REGEX: " + regex);
    }

    /*public void updateRegex(String s , String replacement)
    {
        regex = regex.replaceAll(s, replacement);
    }*/

     /*public void printMatched()
    {
        for (int i=0; i<matchedLines.size(); i++)
        {
            System.out.println(matchedLines.get(i));
        }
    }*/

   /* public int getNumberOfMatchedLines()
    {
        return matchedLines.size();
    }*/

    /*private void extractParts(String def)
    {
        Pattern pattern = Pattern.compile("(<[A-Za-z0-9\\.\\,\\_\\-\\\\\\/\\(\\)\\[\\]\\{\\}]+>|(broj_telefona)|(mejl_adresa)|(web_link)|(brojevna_konstanta)|(veliki_grad))");
        Matcher matcher = pattern.matcher(definition);
        int i=0;
        while(matcher.find())
        {
                parts.add(i++, new Node(token));
        }
    }*/

    public class Node
    {
        public String part;
        public String matchedPart;
        Node(String part)
        {
            this.part= part;
        }
        void setMatchedPart(String s)
        {
            matchedPart = s;
        }
    }
}
