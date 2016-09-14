package com.moffatt.xander.familymap.model;

/**
 * Object for lines between events.
 * Inherits from LineSetting.
 * Stored in events.
 * Created by Xander on 7/30/2016.
 */
public class Line extends LineSetting {
    private String sourceEventID;
    private String destEventID;
    private String destPersonID;

    public Line() {
        sourceEventID = new String();
        destEventID = new String();
    }

    public Line(int color, boolean isDrawn, LINE_TYPE type, String source, String destE, String destP) {
        super(color, isDrawn, type);
        sourceEventID = source;
        destEventID = destE;
        destPersonID = destP;
    }

    public String getSourceEventID() {
        return sourceEventID;
    }

    public void setSourceEventID(String sourceEventID) {
        this.sourceEventID = sourceEventID;
    }

    public String getDestEventID() {
        return destEventID;
    }

    public void setDestEventID(String destEventID) {
        this.destEventID = destEventID;
    }

    public String getDestPersonID() {
        return destPersonID;
    }

    public void setDestPersonID(String destPersonID) {
        this.destPersonID = destPersonID;
    }
}
