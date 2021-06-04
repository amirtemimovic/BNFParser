package org.unibl.etf.fmsi.bnfparser;

//import java.io.*;
//import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.util.zip.GZIPInputStream;

public class BNFFileReader {
    private String[] arrBNF;
    public List<BNFRules> rules = new ArrayList<>();
    private List<String> tokens = new ArrayList<>();
    int counter = 0;

    public BNFFileReader(String[] arrBNF) {
        this.arrBNF = arrBNF;
    }

    public void read()
    {
        split();
        findDuplicateTokens();
        standardRegexReplacement();
        removeDuplicates();
        regexToBrackets();
        tokenization();

        for (int i = 0; i < rules.size(); i++) {
            String temp = rules.get(i).getToken().replaceAll("<|>", "");
            rules.get(i).token = temp;
        }


    }

    public void returnRegex(String s) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).token == s)
                s = rules.get(i).regex;
        }
    }


    public void split() {
        try {
            String rest, token;
            String[] tokensValue = arrBNF;
            int numberOfST = 0;
            for (int i = 0; i < arrBNF.length; i++) {
                tokensValue = arrBNF[i].split("\\s?::=\\s?");
                if (tokensValue.length != 2) {
                    System.err.println("Error in " + (i + 1) + ". line: " + arrBNF[i]);
                    System.exit(1);
                }
                Pattern patternRest, pattern = Pattern.compile("<[\\w]+>");
                Matcher matcherRest, matcher = pattern.matcher(tokensValue[0]);
                if (matcher.find()) {
                    token = matcher.group();
                    tokens.add(i, token);
                    rest = tokensValue[1];
                    patternRest = Pattern.compile("((<[a-z\\-\\_]+>)|(\\\".+\\\")|(broj_telefona)|(mejl_adresa)|(web_link)|(brojevna_konstanta)|(veliki_grad)|( )|(\\|)|(regex\\(.+\\)))+$");
                    matcherRest = patternRest.matcher(rest);
                    if (matcherRest.find())
                    {
                        rules.add(i, new BNFRules(tokens.get(i), rest, rest));
                    } else
                    {
                        System.err.println("Error2 in " + (i + 1) + ". line: " + arrBNF[i]);
                        System.exit(1);
                    }
                } else
                {
                    System.err.println("Error1 in " + (i + 1) + ". line: " + arrBNF[i]);
                    System.exit(1);
                }
            }
        } catch (IndexOutOfBoundsException ex)
        {
            ex.printStackTrace(System.err);
        }
    }

    private void standardRegexReplacement()
    {
        StandardRegex standardRegex = new StandardRegex();
        standardRegex.bigCityWebCrawler();
        LinkedList<BNFRules> standardTokens = new LinkedList<>();
        for (int i = rules.size()-1; i >= 0; i--)
        {
            String temp = "";
            String searchStandardRegexToken = "(broj_telefona)|(mejl_adresa)|(web_link)|(brojevna_konstanta)|(veliki_grad)";
            Pattern pattern = Pattern.compile(searchStandardRegexToken, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(rules.get(i).getDefinition());

            while (matcher.find()) {
                if (matcher.group(1) != null)
                {
                    temp = rules.get(i).getRegex().replace(matcher.group(1), "(" + standardRegex.getPhoneNumber() + ")");
                    rules.get(i).regex = temp;
                    if(!(isAlone(rules.get(i).getDefinition())))
                    {
                        temp = rules.get(i).getDefinition().replace(matcher.group(1), "<" + matcher.group(1) + ">");
                        rules.get(i).definition = temp;
                    }
                    /*if(!(rules.contains(new BNFRules("<" + matcher.group(1) + ">","<" + matcher.group(1) + ">", standardRegex.getPhoneNumber()))))
                    {
                        rules.add(new BNFRules("<" + matcher.group(1) + ">", "<" + matcher.group(1) + ">", standardRegex.getPhoneNumber()));
                    }*/
                    standardTokens.add(new BNFRules("<" + matcher.group(1) + ">", matcher.group(1), standardRegex.getPhoneNumber()));

                } else if (matcher.group(2) != null)
                {
                    temp = rules.get(i).getRegex().replace(matcher.group(2), "(" + standardRegex.getMailAddress() + ")");
                    rules.get(i).regex = temp;
                    if(!(isAlone(rules.get(i).getDefinition())))
                    {
                        temp = rules.get(i).getDefinition().replace(matcher.group(2), "<" + matcher.group(2) + ">");
                        rules.get(i).definition = temp;
                    }
                    /*if(!(rules.contains(new BNFRules("<" + matcher.group(2) + ">", "<" + matcher.group(2) + ">", standardRegex.getMailAddress()))))
                    {
                        rules.add(new BNFRules("<" + matcher.group(2) + ">", "<" + matcher.group(2) + ">", standardRegex.getMailAddress()));
                    }*/
                    standardTokens.add(new BNFRules("<" + matcher.group(2) + ">", matcher.group(2), standardRegex.getMailAddress()));

                } else if (matcher.group(3) != null)
                {
                    temp = rules.get(i).getRegex().replace(matcher.group(3), "(" + standardRegex.getWeb_link() + ")");
                    rules.get(i).regex = temp;
                    if(!(isAlone(rules.get(i).getDefinition())))
                    {
                        temp = rules.get(i).getDefinition().replace(matcher.group(3), "<" + matcher.group(3) + ">");
                        rules.get(i).definition = temp;
                    }
                    /*if(!(rules.contains(new BNFRules("<" + matcher.group(3) + ">", "<" + matcher.group(3) + ">", standardRegex.getWeb_link()))))
                    {
                        rules.add(new BNFRules("<" + matcher.group(3) + ">", "<" + matcher.group(3) + ">", standardRegex.getWeb_link()));
                    }*/
                    standardTokens.add(new BNFRules("<" + matcher.group(3) + ">", matcher.group(3), standardRegex.getWeb_link()));

                } else if (matcher.group(4) != null)
                {
                    temp = rules.get(i).getRegex().replace(matcher.group(4), "(" + standardRegex.getNumberConstant() + ")");
                    rules.get(i).regex = temp;
                    if(!(isAlone(rules.get(i).getDefinition())))
                    {
                        temp = rules.get(i).getDefinition().replace(matcher.group(4), "<" + matcher.group(4) + ">");
                        rules.get(i).definition = temp;
                    }
                    /*if(!(rules.contains(new BNFRules( "<" + matcher.group(4) + ">" , "<" + matcher.group(4) + ">", standardRegex.getNumberConstant()))))
                    {
                        rules.add(new BNFRules("<" + matcher.group(4) + ">", "<" + matcher.group(4) + ">", standardRegex.getNumberConstant()));
                    }*/
                    standardTokens.add(new BNFRules("<" + matcher.group(4) + ">", matcher.group(4), standardRegex.getNumberConstant()));

                } else if (matcher.group(5) != null)
                {
                    temp = rules.get(i).getRegex().replace(matcher.group(5), "(" + standardRegex.getBigCity() + ")");
                    rules.get(i).regex = temp;
                    if(!(isAlone(rules.get(i).getDefinition())))
                    {
                        temp = rules.get(i).getDefinition().replace(matcher.group(5), "<" + matcher.group(5) + ">");
                        rules.get(i).definition = temp;
                    }
                    /*if(!(rules.contains(new BNFRules("<" + matcher.group(5) + ">", "<" + matcher.group(5) + ">", standardRegex.getBigCity()))))
                    {
                        rules.add(new BNFRules("<" + matcher.group(5) + ">", "<" + matcher.group(5) + ">", standardRegex.getBigCity()));
                    }*/
                    standardTokens.add(new BNFRules("<" + matcher.group(5) + ">", matcher.group(5), standardRegex.getBigCity()));
                }
            }
        }

        for(int i=0; i<standardTokens.size(); i++)
            rules.add(standardTokens.get(i));
    }

    private void removeDuplicates()
    {
        for(int i=0; i<rules.size(); i++)
        {
            for(int j=i+1; j<rules.size(); j++)
            {
                if((rules.get(i).definition.equals(rules.get(j).definition))&(rules.get(i).token.equals(rules.get(j).token))&(rules.get(i).regex.equals(rules.get(j).regex)))
                    rules.remove(j);
            }
        }
    }

    private void standardRegexToToken()
    {
        StandardRegex standardRegex = new StandardRegex();
        standardRegex.bigCityWebCrawler();
        for (int i=rules.size() - 1; i >= 0; i--)
        {
            String temp = "";
            String searchStandardRegexToken = "(broj_telefona)|(mejl_adresa)|(web_link)|(brojevna_konstanta)|(veliki_grad)";
            Pattern pattern = Pattern.compile(searchStandardRegexToken, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(rules.get(i).getDefinition());

            while (matcher.find())
            {
                if (matcher.group(1) != null)
                {
                    temp = rules.get(i).definition.replace(matcher.group(1), "<" + matcher.group(1) + ">");
                    rules.get(i).definition = temp;
                    temp= rules.get(i).regex.replace(matcher.group(1), "(" + standardRegex.getPhoneNumber() + ")");
                    rules.get(i).regex = temp;

                    if(!(rules.contains(new BNFRules("<" + matcher.group(1) + ">", "<" + matcher.group(1) + ">", standardRegex.getPhoneNumber()))))
                    {
                        rules.add(new BNFRules("<" + matcher.group(1) + ">", "<" + matcher.group(1) + ">", standardRegex.getPhoneNumber()));
                    }
                } else if (matcher.group(2) != null)
                {
                    temp = rules.get(i).getDefinition().replace(matcher.group(2), "<" + matcher.group(2) + ">");
                    rules.get(i).definition = temp;
                    temp= rules.get(i).regex.replace(matcher.group(2), "(" + standardRegex.getMailAddress() + ")");
                    rules.get(i).regex = temp;

                    if(!(rules.contains(new BNFRules(matcher.group(2), matcher.group(2), standardRegex.getMailAddress()))))
                    {
                        rules.add(new BNFRules("<" + matcher.group(2) + ">", "<" + matcher.group(2) + ">", standardRegex.getMailAddress()));
                    }
                } else if (matcher.group(3) != null)
                {
                    temp = rules.get(i).getDefinition().replace(matcher.group(3), "<" + matcher.group(3) + ">");
                    rules.get(i).definition = temp;
                    temp= rules.get(i).regex.replace(matcher.group(3), "(" + standardRegex.getWeb_link() + ")");
                    rules.get(i).regex = temp;

                    if(!(rules.contains(new BNFRules(matcher.group(3), matcher.group(3), standardRegex.getWeb_link()))))
                    {
                        rules.add(new BNFRules("<" + matcher.group(3) + ">","<" + matcher.group(3) + ">", standardRegex.getWeb_link()));
                    }
                } else if (matcher.group(4) != null)
                {
                    temp = rules.get(i).getDefinition().replace(matcher.group(4), "<" + matcher.group(4) + ">");
                    rules.get(i).definition= temp;
                    temp= rules.get(i).regex.replace(matcher.group(4), "(" + standardRegex.getNumberConstant() + ")");
                    rules.get(i).regex = temp;

                    if(!(rules.contains(new BNFRules( "<" + matcher.group(4) + ">" , matcher.group(4), standardRegex.getNumberConstant()))))
                    {
                        rules.add(new BNFRules("<" + matcher.group(4) + ">", "<" + matcher.group(4) + ">", standardRegex.getNumberConstant()));
                    }
                } else if (matcher.group(5) != null)
                {
                    temp = rules.get(i).getDefinition().replace(matcher.group(5), "<" + matcher.group(5) + ">");
                    rules.get(i).definition = temp;
                    temp= rules.get(i).regex.replace(matcher.group(5), "(" + standardRegex.getBigCity() + ")");
                    rules.get(i).regex = temp;

                    if(!(rules.contains(new BNFRules("<" + matcher.group(5) + ">", "<" + matcher.group(5) + ">", standardRegex.getBigCity()))))
                    {
                        rules.add(new BNFRules("<" + matcher.group(5) + ">", "<" + matcher.group(5) + ">", standardRegex.getBigCity()));
                    }
                }
            }
        }
    }

    private void addStandardTokens()
    {
        for (int i = 0; i< rules.size(); i++)
        {
            StandardRegex standardRegex = new StandardRegex();
            String temp = "";
            String searchStandardRegexToken = "(<broj_telefona>)|(<mejl_adresa>)|(<web_link>)|(<brojevna_konstanta>)|(<veliki_grad>)";
            Pattern pattern = Pattern.compile(searchStandardRegexToken, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(rules.get(i).getDefinition());

            while (matcher.find())
            {
                if (matcher.group(1) != null)
                {
                    temp = rules.get(i).getDefinition().replace(matcher.group(1), matcher.group(1));
                    rules.get(i).definition = temp;

                    if(!(rules.contains(new BNFRules(matcher.group(1), matcher.group(1), new StandardRegex().getPhoneNumber()))))
                    {
                        rules.add(new BNFRules(matcher.group(1), matcher.group(1), new StandardRegex().getPhoneNumber()));
                    }
                } else if (matcher.group(2) != null)
                {
                    temp = rules.get(i).getDefinition().replace(matcher.group(2), matcher.group(2));
                    rules.get(i).definition = temp;

                    if(!(rules.contains(new BNFRules(matcher.group(2), matcher.group(2), new StandardRegex().getMailAddress()))))
                    {
                        rules.add(new BNFRules(matcher.group(2), matcher.group(2), new StandardRegex().getPhoneNumber()));
                    }
                } else if (matcher.group(3) != null)
                {
                    temp = rules.get(i).getDefinition().replace(matcher.group(3), matcher.group(3));
                    rules.get(i).definition = temp;

                    if(!(rules.contains(new BNFRules(matcher.group(3), matcher.group(3), new StandardRegex().getWeb_link()))))
                    {
                        rules.add(new BNFRules(matcher.group(3), matcher.group(3), new StandardRegex().getWeb_link()));
                    }
                } else if (matcher.group(4) != null)
                {
                    temp = rules.get(i).getDefinition().replace(matcher.group(4), matcher.group(4));
                    rules.get(i).definition= temp;

                    if(!(rules.contains(new BNFRules( matcher.group(4), matcher.group(4), new StandardRegex().getNumberConstant()))))
                    {
                        rules.add(new BNFRules(matcher.group(4), matcher.group(4), new StandardRegex().getNumberConstant()));
                    }
                } else if (matcher.group(5) != null)
                {
                    temp = rules.get(i).getDefinition().replace(matcher.group(5), matcher.group(5));
                    rules.get(i).definition = temp;

                    if(!(rules.contains(new BNFRules(matcher.group(5), matcher.group(5), new StandardRegex().getBigCity()))))
                    {
                        rules.add(new BNFRules(matcher.group(5), matcher.group(5), new StandardRegex().getBigCity()));
                    }
                }
            }
        }
    }

    private void regexToBrackets() {
        for (int i = rules.size() - 1; i >= 0; i--) {
            String searchRegex = "(regex\\(([\\w\\-\\|\\[\\]\\/\\(\\)\\.\\,\\<\\>\\+\\!\\?\\#\\%\\&\\\\]*)\\))";
            Pattern pattern = Pattern.compile((searchRegex), Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(rules.get(i).getRegex());
            while (matcher.find()) {
                String temp = rules.get(i).regex.replace(matcher.group(1), matcher.group(2));
                rules.get(i).regex = temp;
            }
        }
    }

    private void tokenization() {
        for (int i = rules.size() - 1; i >= 0; i--) {
            String searchTokens = "<[a-z_]+>";
            Pattern pattern = Pattern.compile(searchTokens, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(rules.get(i).getDefinition());

            while (matcher.find()) {
                for (int j = rules.size() - 1; j >= 0; j--) {
                    if (rules.get(j).getToken().equals(matcher.group(0))) {
                        String temp = rules.get(i).regex.replace(matcher.group(0), "(" + rules.get(j).regex + ")");
                        rules.get(i).regex = temp;
                    }
                }
            }

            String temp = rules.get(i).regex.replaceAll("[\"]", "");
            rules.get(i).regex = temp;
            if (rules.get(i).regex.matches("(?:[\\w\\-\\(\\)\\+\\.\\,\\*\\<\\> ]+\\|?)+")) {
                temp = "(" + rules.get(i).regex + ")";
                rules.get(i).regex = temp;
            }
        }

        addEndline();
        /*for(int i=0; i<rules.size(); i++)
        {
            String temp ="(" + rules.get(i).regex + ")";
            rules.get(i).regex = temp;
        }*/
    }

    private void findDuplicateTokens() {
        for (int i = 0; i < rules.size(); i++) {
            for (int j = i + 1; j < rules.size(); j++) {
                if (rules.get(i).getToken().equals(rules.get(j).getToken())) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String temp;
                    stringBuilder.append(rules.get(i).getDefinition());
                    stringBuilder.append("|");
                    stringBuilder.append(rules.get(j).getDefinition());
                    rules.get(i).definition = stringBuilder.toString();
                    rules.get(i).regex = stringBuilder.toString();
                    rules.remove(j);
                }
            }
        }
    }

    private boolean isAlone(String s)
    {
        String regex = "(broj_telefona)$|(mejl_adresa)$|(web_link)$|(brojevna_konstanta)$|(veliki_grad)$";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()){
           return true;
        }
        return false;
    }


    /*private void removeSpaces()
    {
        String spaceRegex = "(>( +)[\\\"\\<rbmwv])|(\\\"( +)[\\\"\\<rbmwv])|([adk]( +)[\\<\\\"rbmwv])|(\\)( +)[\\<\\\"rbmwv])|( +( ) +)";

        for(int i=0; i<rules.size(); i++)
        {
            Pattern pattern = Pattern.compile(spaceRegex);
            Matcher matcher = pattern.matcher(rules.get(i).regex);
            String temp;
            while (matcher.find())
            {
                if(matcher.group(1) != null)
                {
                    temp = rules.get(i).regex.replace(matcher.group(2), "");
                    rules.get(i).regex = temp;
                }
                else if(matcher.group(3) != null)
                {
                    temp = rules.get(i).regex.replace(matcher.group(4), "");
                    rules.get(i).regex = temp;
                }
                else if(matcher.group(5) != null)
                {
                    temp = rules.get(i).regex.replace(matcher.group(6), "");
                    rules.get(i).regex = temp;
                }
                else if(matcher.group(7) != null)
                {
                    temp = rules.get(i).regex.replace(matcher.group(8), "");
                    rules.get(i).regex = temp;
                }
                else if(matcher.group(9) != null)
                {
                    temp = rules.get(i).regex.replace(matcher.group(10), "");
                    rules.get(i).regex = temp;
                }
                else if(matcher.group(10) != null)
                {
                    temp = rules.get(i).regex.replace(matcher.group(11), "");
                    rules.get(i).regex = temp;
                }
            }
        }
    }*/

    /*private void replaceSigns()
    {
        String temp, regex = "\\\"([\\.])*([\\+])*([\\*])*\\\"";

        for(int i=0; i<rules.size(); i++)
        {
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(rules.get(i).regex);

            while (matcher.find())
            {
                if(matcher.group(1)!= null)
                {
                    temp= rules.get(i).regex.replace(".", "[.]");
                    rules.get(i).regex = temp;
                }
                else if(matcher.group(2) != null)
                {
                    temp = rules.get(i).regex.replace("+", "[+]");
                    rules.get(i).regex = temp;
                }
                else if(matcher.group(3) != null)
                {
                    temp = rules.get(i).regex.replace("*", "[*]");
                    rules.get(i).regex = temp;
                }
            }
        }
    }*/

    /*private void addEndline()
    {
        for(int i=0; i<rules.size(); i++)
        {
            String temp = "((" + rules.get(i).regex.concat(" ?)+)$");
            rules.get(i).regex = temp;
        }
    }*/

    private void addEndline()
    {
        if(rules.size()>0)
        {
            String temp = "(" + rules.get(0).regex.concat(")");
            rules.get(0).regex = temp;
        }
    }
}
