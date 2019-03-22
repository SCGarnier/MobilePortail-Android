package com.briccsquad.mobileportail.session;

import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

/**
 * List of schools with this app's support. If logging in and the school
 * string does not match anything here, the app may or may not crash.
 *
 * @author xprogram
 */
public enum School {
    UNKNOWN(null), // Nice. You got me.
    SCG("https://esscg.cscmonavenir.ca/"); // Saint-Charles-Garnier @ Whitby

    /**
     * School website base URL.
     */
    public final String website;

    School(String weburl) {
        website = weburl;
    }

    private static final Logger logger = LoggerManager.getLogger(School.class);

    /**
     * Converts the portail's school name to a suitable enum.
     *
     * @param s The string to check for a matching enum.
     * @return The associated school enum.
     */
    public static School conv(String s) {
        switch (s) {
            case "ÉSC Saint-Charles-Garnier - Whitby":
                return SCG;
            default:
                logger.w("Unknown school ID was given: " + s);
                return UNKNOWN;
        }
    }
}
