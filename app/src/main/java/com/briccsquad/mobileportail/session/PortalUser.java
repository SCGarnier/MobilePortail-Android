package com.briccsquad.mobileportail.session;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds information relative to a user that has logged in. Said information
 * includes:
 * <ul>
 * <li>the school they attend</li>
 * <li>their current grades</li>
 * <li>their schedule (divided into periods with the teachers' names)</li>
 * </ul>
 *
 * @author xprogram
 */
public class PortalUser {
    private static PortalUser current;

    static void setCurrent(PortalUser portalUser) {
        current = portalUser;
    }

    public static PortalUser getCurrent() {
        return current;
    }

    /**
     * A list of class identifiers. The format for the string goes:
     * - class name
     * - newline character
     * - teacher name
     */
    private final List<String> classList = new ArrayList<>();

    private final List<PortalUserGrade> gradeList = new ArrayList<>();

    private final List<Period> periodList = new ArrayList<>();

    private final School school;

    private final String uname;

    /**
     * Construct a new profile for a logged-in user.
     *
     * @param c               The class list bound to the user.
     * @param g               The grades bound to the user.
     * @param attendingSchool The school the user attends.
     */
    public PortalUser(List<String> c, List<PortalUserGrade> g, String attendingSchool, String uname) {

        // Perform a copy to avoid any external tampering of data
        classList.addAll(c);
        gradeList.addAll(g);

        school = School.conv(attendingSchool);

        this.uname = uname;
    }

    public List<PortalUserGrade> getGradesForClass(int index) {
        List<PortalUserGrade> arr = new ArrayList<>();
        for (PortalUserGrade grade : gradeList) {
            if (grade.getPortalClassIndex() == index) {
                arr.add(grade);
            }
        }

        return arr;
    }

    public int getClassListSize() {
        return classList.size();
    }

    public String getGradeClassInfo(int i) {
        return classList.get(i);
    }

    public List<String> getSummaryLinks() {
        List<String> arr = new ArrayList<>();
        for (PortalUserGrade grade : gradeList) {
            arr.add(grade.getSummaryLink());
        }

        return arr;
    }

    public void setSummaryFile(int i, String summaryFilename) {
        gradeList.get(i).setSummaryFile(summaryFilename);
    }

    public String getSummaryFile(int i) {
        return gradeList.get(i).getSummaryFile();
    }

    public void addPeriod(@NonNull String className,
                          @NonNull String teacherName,
                          @NonNull String place) {
        Period period = new Period();
        period.className = className;
        period.teacherName = teacherName;
        period.place = place;
        periodList.add(period);
    }

    public String getPeriodTeacher(int i) {
        return periodList.get(i).teacherName;
    }

    public String getPeriodClass(int i) {
        return periodList.get(i).className;
    }

    public String getPeriodPlace(int i) {
        return periodList.get(i).place;
    }

    public int getNumPeriods() {
        return periodList.size();
    }

    public School getSchool() {
        return school;
    }

    public String getLoginName() {
        return uname;
    }

    private static class Period {
        String className;
        String place;
        String teacherName;
    }

}
