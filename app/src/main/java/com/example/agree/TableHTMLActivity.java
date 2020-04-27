package com.example.agree;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TableHTMLActivity extends AppCompatActivity {

    WebView webViewHTML;
    boolean showFullTable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showFullTable = true;
        setContentView(R.layout.activity_html_table);
        final Button setTableView = findViewById(R.id.button_change_html);
        webViewHTML = (WebView) findViewById(R.id.text_html);
        WebSettings webSettings = webViewHTML.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        String messageID = getIntent().getStringExtra("messageID");
        final String contractorNum = getIntent().getStringExtra("agrNumInMail");
        MessageAgree messageAgree = MainActivity.mailTask.getMessageAgreeForId(messageID);

        final String htmlString = messageAgree.getHtmlContent();
       //final String contractorNum = messageAgree.getAgrNumInMail();


        //if(showFullTable){
            //webViewHTML.loadDataWithBaseURL(null, htmlString, "text/html", "windows-1251", null);
        //}else {
            String htmlHead = reformatHTML(htmlString, contractorNum);
            webViewHTML.loadDataWithBaseURL(null, htmlHead, "text/html", "windows-1251", null);
        //}
        webViewHTML.setInitialScale(95);

        setTableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showFullTable){
                    showFullTable = false;
                    setTableView.setText("Сокращённо");
                    webViewHTML.loadDataWithBaseURL(null, htmlString, "text/html", "windows-1251", null);
                }else {
                    showFullTable = true;
                    setTableView.setText("Письмо полностью");
                    String htmlHead = reformatHTML(htmlString, contractorNum);
                    webViewHTML.loadDataWithBaseURL(null, htmlHead, "text/html", "windows-1251", null);
                }
                webViewHTML.setInitialScale(95);
            }
        });
    }



    private String tableData(String inString){
        StringBuilder sb = new StringBuilder();
        Document doc = Jsoup.parse(inString);
        for (Element element : doc.select("td")){
            sb.append(element.text());
        }
        return sb.toString();
    }

    private String reformatHTML(String inString, String contractorNum){
        StringBuilder sb = new StringBuilder();
        String[] stringsMess = inString.split("\n");
        String[] fields = new String[] {"Клиент", "ТП", "Причина", "По текущей команде", "Общее", "Состав заказа",
                "комментарий ТП", "комментарий ФК", "ТП (договор)", "Коммент из карточки клиента"};

        for(String currentString : stringsMess){
            if (!currentString.equals("</head>\r")){
                sb.append(currentString);
            }else {
                sb.append(currentString);
                break;
            }
        }

        sb.append("<body lang=\"RU\" link=\"#0563C1\" vlink=\"#954F72\">\r");
        sb.append("<div class=\"WordSection1\">\r");
        sb.append("<p class=\"MsoNormal\"><span style=\"font-size:11.0pt;font-family:&quot;Calibri&quot;,sans-serif;color:#1F497D;mso-fareast-language:EN-US\"><o:p>&nbsp;</o:p></span></p>\r");
        sb.append("<p class=\"MsoNormal\"><span style=\"font-size:11.0pt;font-family:&quot;Calibri&quot;,sans-serif;color:#1F497D;mso-fareast-language:EN-US\"><o:p>&nbsp;</o:p></span></p>\r");
        sb.append("<p class=\"MsoNormal\"><o:p>&nbsp;</o:p></p>\r");
        sb.append("<table class=\"MsoNormalTable\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\r");
        sb.append("<tbody>\r");

        Document doc = Jsoup.parse(inString);
        Elements listElement = doc.select("td");
        for (int i = 11; i < listElement.size(); ) {
            //if(listElement.get(i).text().equals("Коммент из карточки клиента")){
            if (listElement.get(i).text().equals(contractorNum)) {
                for (int j = 0; j < fields.length; j++) {
                    sb.append("<tr>\r");
                    sb.append("<td style=\"padding:.100pt .75pt .100pt .75pt\">\r");
                    sb.append("<p class=\"MsoNormal\" align=\"center\" style=\"text-align:center; font-size: 250%\"><b>");
                    sb.append(fields[j]);
                    sb.append("</b><o:p></o:p></p>\r");
                    sb.append("</td>\r");
                    sb.append("<td style=\"padding:.100pt .75pt .100pt .75pt\">\r");
                    sb.append("<p class=\"MsoNormal\" align=\"center\" style=\"text-align:center; font-size: 250%\"><b>");
                    sb.append(listElement.get(i + j + 1).text());
                    sb.append("</b><o:p></o:p></p>\r");
                    sb.append("</td>\r");
                    sb.append("</tr>\r");
                }
                //}

            }
            i += 11;
        }
        sb.append("</tbody>\r");
        sb.append("</table>\r");
        sb.append("<p class=\"MsoNormal\"><span style=\"font-size:36.0pt;font-family:&quot;Arial&quot;,sans-serif\"><o:p>&nbsp;</o:p></span></p>\r");
        sb.append("</div>\r");
        sb.append("</body>\r");
        sb.append("</html>\r");
        return sb.toString();
    }
}
