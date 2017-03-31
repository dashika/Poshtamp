
package cf.dashika.poshtamp.Model;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cf.dashika.poshtamp.R;

public class Schedule {

    @SerializedName("mon")
    @Expose
    private String mon;
    @SerializedName("tue")
    @Expose
    private String tue;
    @SerializedName("wed")
    @Expose
    private String wed;
    @SerializedName("thu")
    @Expose
    private String thu;
    @SerializedName("fri")
    @Expose
    private String fri;
    @SerializedName("sat")
    @Expose
    private String sat;
    @SerializedName("sun")
    @Expose
    private String sun;
    @SerializedName("hol")
    @Expose
    private String hol;

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public String getTue() {
        return tue;
    }

    public void setTue(String tue) {
        this.tue = tue;
    }

    public String getWed() {
        return wed;
    }

    public void setWed(String wed) {
        this.wed = wed;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getFri() {
        return fri;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }

    public String getSat() {
        return sat;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public String getSun() {
        return sun;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public String getHol() {
        return hol;
    }

    public void setHol(String hol) {
        this.hol = hol;
    }

    public String toString(Context context)
    {
        return (context.getString(R.string.monday)+mon + "\n"
                + context.getString(R.string.tuesday)+ tue  + "\n"
                + context.getString(R.string.wednesday) + wed  + "\n"
                + context.getString(R.string.thursday)+ thu   + "\n"
                + context.getString(R.string.friday) + fri  + "\n"
                + context.getString(R.string.saturday) + sat  + "\n"
                + context.getString(R.string.sunday) + sun  + "\n").replace("00","-") ;
    }

}
