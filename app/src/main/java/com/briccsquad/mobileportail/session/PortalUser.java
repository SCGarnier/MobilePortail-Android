package com.briccsquad.mobileportail.session;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds information relative to the user that is logged in.
 *
 * @author xprogram
 */
public class PortalUser {
    private static PortalUser current;

    static void setCurrent(PortalUser portalUser){
        current = portalUser;
    }

    public static PortalUser getCurrent(){
        return current;
    }

    /**
     * A list of class identifiers. The format for the string goes:
     *  - class name
     *  - newline
     *  - teacher name
     */
    private final List<String> classList = new ArrayList<>();

    private final List<PortalUserGrade> gradeList = new ArrayList<>();

    private final List<Period> periodList = new ArrayList<>();

    private final String school;

    public PortalUser(List<String> c, List<PortalUserGrade> g, String attendingSchool){
        // Perform a copy to avoid any external tampering of data
        classList.addAll(c);
        gradeList.addAll(g);

        school = attendingSchool;
    }

    public List<PortalUserGrade> getGradesForClass(int index){
        List<PortalUserGrade> arr = new ArrayList<>();
        for(PortalUserGrade grade: gradeList){
            if(grade.getPortalClassIndex() == index){
                arr.add(grade);
            }
        }

        return arr;
    }

    public int getClassListSize(){
        return classList.size();
    }

    public String getGradeClassInfo(int i){
        return classList.get(i);
    }

    public List<String> getSummaryLinks() {
        List<String> arr = new ArrayList<>();
        for(PortalUserGrade grade: gradeList){
            arr.add(grade.getSummaryLink());
        }

        return arr;
    }

    public void setSummaryFile(int i, String summaryFilename) {
        gradeList.get(i).setSummaryFile(summaryFilename);
    }

    public String getSummaryFile(int i){
        return gradeList.get(i).getSummaryFile();
    }

    public void addPeriod(@NonNull String a, @NonNull String b, @NonNull String c) {
        Period period = new Period();
        period.className = a;
        period.teacherName = b;
        period.place = c;
        periodList.add(period);
    }

    public String getPeriodTeacher(int i){
        return periodList.get(i).teacherName;
    }

    public String getPeriodClass(int i){
        return periodList.get(i).className;
    }

    public String getPeriodPlace(int i){
        return periodList.get(i).place;
    }

    public int getNumPeriods(){
        return periodList.size();
    }

    private static class Period {
        String className;
        String place;
        String teacherName;
    }

}
