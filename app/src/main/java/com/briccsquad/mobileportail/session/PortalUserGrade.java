package com.briccsquad.mobileportail.session;

public class PortalUserGrade {
    /**
     * A domain name for people who don't have domains in that class.
     */
    static final String MAIN_DOMAIN_NAME = "@";

    private final int classIndex;
    private String domainName;
    private String average;
    private String pdfLink;
    private String fname;

    PortalUserGrade(int c) {
        classIndex = c;
    }

    void setDomainName(String name) {
        domainName = name;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getRawGradeAverage() {
        return average;
    }

    public void setRawGradeAverage(String g) {
        average = g;
    }

    public int getPortalClassIndex() {
        return classIndex;
    }

    public void setSummaryLink(String jsUrl) {
        pdfLink = jsUrl;
    }

    public String getSummaryLink(){
        return pdfLink;
    }

    public void setSummaryFile(String summaryFilename) {
        fname = summaryFilename;
    }

    public String getSummaryFile() {
        return fname;
    }
}
