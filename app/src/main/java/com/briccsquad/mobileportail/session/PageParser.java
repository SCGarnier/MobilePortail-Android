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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser that specializes in extracting information about a student
 * from the portail's web pages.
 */
class PageParser {

    /**
     * A RegExp used to extract the necessary URL for PDFs from the HTML link source.
     */
    private static final Pattern pdfUrlPattern = Pattern.compile("open\\('(.*)'\\);");

    private final Map<PortalPage, Document> pageDocMap = new HashMap<>();

    public PageParser(@NonNull Map<PortalPage, String> pageData) {
        if (pageData.size() < PortalPage.values().length - 1) {
            throw new IllegalArgumentException("Given map of pages was not complete!");
        }

        for (Map.Entry<PortalPage, String> entry : pageData.entrySet()) {
            pageDocMap.put(entry.getKey(), Jsoup.parse(entry.getValue()));
        }
    }

    private Elements selectGradeLinks() {
        return Objects.requireNonNull(pageDocMap.get(PortalPage.GRADES_TABLE))
                .select("#Table1 a");
    }

    private Elements selectTeacherElements() {
        return Objects.requireNonNull(pageDocMap.get(PortalPage.GRADES_TABLE))
                .select("#Table1 div.text");
    }

    /**
     * Creates a profile of the associated user by parsing the downloaded web pages.
     *
     * @return A {@link PortalUser} describing the associated user.
     * @param username
     */
    public PortalUser createProfile(String username) {
        // Hold off on people with domains...
        // TODO remove this restriction
        if (Objects.requireNonNull(pageDocMap.get(PortalPage.GRADES_TABLE))
                .selectFirst("#Table1 > tbody > tr:nth-child(1) > td:nth-child(2)")
                .text().contentEquals("Domaine")) {
            return null;
        }

        List<PortalUserGrade> gradeList = new ArrayList<>();
        List<String> classList = new ArrayList<>();

        Elements classElems = selectTeacherElements();
        for (Element elem : classElems) {
            classList.add(elem.child(0).text() + "\n" + elem.ownText());
        }

        for (int i = 0, l = classElems.size(); i < l; i++) {
            Element elemClass = classElems.get(i);
            Element elem = elemClass.parent().parent().selectFirst("a");

            if (elem == null) continue;

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
                Objects.requireNonNull(pageDocMap.get(PortalPage.SCHOOL_INFO))
                        .select("#LNomEcole").text(), username);

        setupSchedule(user);
        return user;
    }

    private void setupSchedule(PortalUser p) {
        Elements trList = Objects.requireNonNull(pageDocMap.get(PortalPage.SCHEDULE))
                .select("table.text tr");

        int activeDay = 0;

        Elements testRow = trList.get(1).select("td");

        // Compute active day from schedule style rules, since it's the only hint
        // that the day changed...
        for (int i = 1, len = testRow.size(); i < len; i++) {
            if (!testRow.get(i).attr("bgcolor").isEmpty()) {
                activeDay = i;
                break;
            }
        }

        // No school today?
        if (activeDay == 0) {
            return;
        }

        for (int i = 1, len = trList.size(); i < len; i++) {
            Element periodElem = trList.get(i).select("td").get(activeDay).child(0);
            List<TextNode> wholeTextList = periodElem.textNodes();

            // Attempt to decode the schedule data, otherwise just
            // try not to crash
            try {
                p.addPeriod(wholeTextList.get(0).text().trim(),
                        wholeTextList.get(1).text().trim(),
                        wholeTextList.get(2).text().trim());
            } catch (Exception e) {
                p.addPeriod("???", "???", "???");
            }
        }
    }
}
