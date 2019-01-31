package com.briccsquad.mobileportail.session;

public enum PortalPage {
    SCHEDULE("/PortailEleves/EmploiDuTemps.aspx"),
    GRADES_TABLE("/PortailEleves/AccueilEleve.aspx"),
    SCHOOL_INFO("/PortailEleves/InfoEcole.aspx"),
    SPECIAL(null);

    private final String serverPath;
    PortalPage(String path){
        serverPath = path;
    }

    public String getServerPath(){
        return serverPath;
    }
}
