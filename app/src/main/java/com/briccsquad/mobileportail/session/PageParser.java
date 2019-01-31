package com.briccsquad.mobileportail.session;

import android.support.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PageParser {
    /**
     * A RegExp used to extract the necessary URL for PDFs from the HTML link source.
     */
    private static final Pattern pdfUrlPattern = Pattern.compile("open\\('(.*)'\\);");

    private final Map<PortalPage, Document> pageDocMap = new HashMap<>();

    public PageParser(@NonNull Map<PortalPage, String> pageData) {
        if(pageData.size() < PortalPage.values().length - 1){
            throw new IllegalArgumentException("Given map of pages was not complete!");
        }

        for (Map.Entry<PortalPage, String> entry : pageData.entrySet()) {
            pageDocMap.put(entry.getKey(), Jsoup.parse(entry.getValue()));
        }
    }

    private Elements selectGradeLinks(){
        return pageDocMap.get(PortalPage.GRADES_TABLE).select("#Table1 a");
    }

    private Elements selectTeacherElements(){
        return pageDocMap.get(PortalPage.GRADES_TABLE).select("#Table1 div.text");
    }

    public PortalUser createProfile() {
        // Hold off on people with domains...
        // TODO remove this restriction
        if(pageDocMap.get(PortalPage.GRADES_TABLE)
                .selectFirst("#Table1 > tbody > tr:nth-child(1) > td:nth-child(2)")
                .text().contentEquals("Domaine")){
            return null;
        }

        List<PortalUserGrade> gradeList = new ArrayList<>();
        List<String> classList = new ArrayList<>();

        Elements classElems = selectTeacherElements();
        for(Element elem: classElems){
            classList.add(elem.child(0).text() + "\n" + elem.ownText());
        }

        for(int i = 0, l = classElems.size(); i < l; i++){
            Element elemClass = classElems.get(i);
            Element elem = elemClass.parent().parent().selectFirst("a");

            if(elem == null) continue;

            PortalUserGrade p = new PortalUserGrade(i);
            p.setDomainName(PortalUserGrade.MAIN_DOMAIN_NAME);
            p.setRawGradeAverage(elem.text());

            // Find a link to PDF for class statistics
            Matcher m = pdfUrlPattern.matcher(elem.attr("onclick"));
            if (m.find()) {
                String jsUrl = m.group(1);
                p.setSummaryLink(jsUrl);
            }

            gradeList.add(p);
        }

        PortalUser user = new PortalUser(classList, gradeList,
                pageDocMap.get(PortalPage.SCHOOL_INFO)
                        .select("#LNomEcole").text());

        setupSchedule(user);
        return user;
    }

    private void setupSchedule(PortalUser p) {
        Elements trList = pageDocMap.get(PortalPage.SCHEDULE).select("table.text tr");

        // TODO compute value from schedule
        int activeDay = 1;

        for (int i = 1, len = trList.size(); i < len; i++) {
            List<TextNode> wholeTextList = trList.get(i).select("td").get(activeDay).textNodes();
            if(!wholeTextList.isEmpty()){
                p.addPeriod(wholeTextList.get(0).text().trim(),
                        wholeTextList.get(1).text().trim(),
                        wholeTextList.get(2).text().trim());
            } else {
                p.addPeriod("???", "???", "???");
            }
        }
    }
}
