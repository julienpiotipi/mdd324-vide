package helloworld;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.json.JSONObject;
import org.json.XML;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Object, Object> {

    public Object handleRequest(final Object input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        try {
            final String pageContents = this.getPageContents("https://www.lemonde.fr/rss/une.xml");
            JSONObject xmlJSONObj = XML.toJSONObject(pageContents);
            String jsonPrettyPrintString = xmlJSONObj.toString(4);
            List<Article> list = new ArrayList<>();
            for(int i=0; i<=9; i++){
                Article a = new Article();
                a.setLink(xmlJSONObj.getJSONObject("rss").getJSONObject("channel").getJSONArray("item").getJSONObject(i).getString("link"));
                a.setTitle(xmlJSONObj.getJSONObject("rss").getJSONObject("channel").getJSONArray("item").getJSONObject(i).getString("title"));
                a.setDescription(xmlJSONObj.getJSONObject("rss").getJSONObject("channel").getJSONArray("item").getJSONObject(i).getString("description"));
                a.setPubDate(xmlJSONObj.getJSONObject("rss").getJSONObject("channel").getJSONArray("item").getJSONObject(i).getString("pubDate"));
                list.add(a);
            }
            /*System.out.println(jsonPrettyPrintString);
            String output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents);*/
            System.out.println(list);
            return list;
        } catch (IOException e) {
            return new GatewayResponse("{}", headers, 500);
        }
    }

    private String getPageContents(String address) throws IOException{
        BufferedReader br = null;
        StringJoiner lines = new StringJoiner(System.lineSeparator());
        try {
            URL url = new URL(address);
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return lines.toString();
    }
}
