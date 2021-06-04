package org.unibl.etf.fmsi.bnfparser;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class StandardRegex
{
    private String broj_telefona = "(?:([+]|(00))?(?:(?:3876[0-9])|(?:3876[0-9]))(?:[\\/\\-]| )?(?:[0-9]){3}(?:[\\/\\-]| )?(?:[0-9]){3})|(?:(?:06[0-9])(?:[\\/\\-]| )?(?:[0-9]){3}(?:[\\/\\-]| )?(?:[0-9]){3})";
    private String mejl_adresa = "[\\w\\-.]+@[\\w\\-]+.[A-Za-z]{2,10}(?:[.][A-Za-z]{2,8})?";
    private String web_link = "https?:\\/\\/(www[.])?[-a-zA-Z0-9@:%._\\+~#=]{1,256}.[a-zA-Z0-9()]{1,6}\\b(?:\\/[\\-a-zA-Z0-9()@:%_\\/\\+.~#?&=]*)";
    private String brojevna_konstanta = "[0-9]+(\\.[0-9]+)?";
    private String veliki_grad;

    public  String getPhoneNumber()
    {
        return  broj_telefona;
    }

    public String getMailAddress()
    {
        return mejl_adresa;
    }

    public String getWeb_link()
    {
        return web_link;
    }

    public String getNumberConstant()
    {
        return brojevna_konstanta;
    }

    public String getBigCity() { return veliki_grad;}

    public void bigCityWebCrawler()
    {
        try {

            Scanner scanner = new Scanner(new GZIPInputStream(new URL("http://worldpopulationreview.com/continents/cities-in-europe/").openStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String webPage;

            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
            }

            webPage = stringBuilder.toString();
            int counter = 1;

            final String regex = "<td>([A-Z][a-z ]+)<\\/td>";
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(webPage);

            StringBuilder temp = new StringBuilder();
            temp.append("Istanbul");
            while (matcher.find() && counter <= 200) {
                temp.append("|");
                temp.append(matcher.group(1));
                counter++;
            }

            veliki_grad = temp.toString();

            //System.out.println(veliki_grad);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
