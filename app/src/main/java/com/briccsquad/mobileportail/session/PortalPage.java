package com.briccsquad.mobileportail.session;

/**
 * A list of server endpoints that return useful information about a user when
 * correct authentication details are provided when requesting.
 *
 * @author xprogram
 */
public enum PortalPage {
    SCHEDULE("/PortailEleves/EmploiDuTemps.aspx"),
    GRADES_TABLE("/PortailEleves/AccueilEleve.aspx"),
    SCHOOL_INFO("/PortailEleves/InfoEcole.aspx"),
    SPECIAL(null);

    /**
     * The actual path to request when communicating to the server.
     */
    private final String serverPath;

    PortalPage(String path) {
        serverPath = path;
    }

    public String getServerPath() {
        return serverPath;
    }
}
