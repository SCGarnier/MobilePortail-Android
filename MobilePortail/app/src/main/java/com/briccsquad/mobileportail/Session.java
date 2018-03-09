package com.briccsquad.mobileportail;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * This class holds information relative to the user that is logged in.
 * @author xprogram
 */
public class Session {
    public static String portailGradesContent;
    public static String portailScheduleContent;
    public static ArrayList<String> portailSumPages;

    // TODO: Replace with fixed size array
    public static final ArrayList<String> teacherDayList = new ArrayList<>();
    public static final ArrayList<String> classDayList = new ArrayList<>();

    public static final ArrayList<String> notesInfoList = new ArrayList<>();
    public static final ArrayList<Domain> domainList = new ArrayList<>();

    /**
     * Call this whenever 'portailScheduleContent' changed.
     */
    public static void computeScheduleData(){
        classDayList.clear();
        teacherDayList.clear();

        Elements trList = Jsoup.parse(portailScheduleContent).select("table.text tr");
        int activeDay = 1;

        for(int i = 1, len = trList.size(); i < len; i++) {
            if(i == 3 && len == 6) continue; // Avoid duplicate
            String[] txt = trList.get(i).select("td").get(activeDay).wholeText().split("\n");
            classDayList.add(txt[2].trim());
            teacherDayList.add(txt[3].trim());
        }
    }

    /**
     * Call this whenever 'portailGradesContent' changed.
     */
    public static void computeGradesData(){
        notesInfoList.clear();
        domainList.clear();

        int j = 0;

        Document doc = Jsoup.parse(portailGradesContent);
        Elements trList = doc.select("#Table1 tr");

        final boolean considerDomains =
                doc.selectFirst("#Table1 > tbody > tr:nth-child(1) > td:nth-child(2)")
                        .text().contentEquals("Domaine");

        int currentDataIndex = 0;
        for(int i = 1; i < trList.size(); i++){
            Element classData = trList.get(i);

            // Now, things get complicated here. Some portail profiles will have
            // specific domains for each criteria, and the table is not properly
            // formatted to hold details like that. What's worse is that there's an
            // average in each criteria, not one single string we can display
            // for the class. We must handle this separately, and also make sure the
            // data is normalized to fit the variables above.

            // Is the student still in the class? If false, there
            // should be a font element in the DOM.
            //final boolean inClass = (classData.selectFirst("font") == null);

            // Is this a domain delimiter?
            if(!classData.selectFirst("td").hasText()) continue;

            // Mark the occurrence of a new class to parse
            Element classInfoElem = classData.selectFirst("div.text");
            if(classInfoElem != null){
                currentDataIndex = i;

                // Get the class name
                String className = classInfoElem.text();

                // Add a question mark if there was no teacher specified
                if (className.endsWith(":")) {
                    className += " ?";
                }

                notesInfoList.add(className);
            }

            // Get grade
            Element avgElem = classData.selectFirst("a");

            // Insert new domain with grade
            Domain domain = new Domain();
            domain.grade = (avgElem != null) ? avgElem.text() : null;
            domain.info = (considerDomains) ? classData.selectFirst("span.text").text() : null;
            domain.reportTo = notesInfoList.size() - 1;
            domain.summaryFileName = (avgElem != null) ? portailSumPages.get(j++) : null;
            domainList.add(domain);
        }
    }

    public static class Domain {
        public String info;
        public String grade;
        public int reportTo;
        public String summaryFileName;
    }
}
