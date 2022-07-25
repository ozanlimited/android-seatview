package com.kokozu.widget.samples;

import com.alibaba.fastjson.annotation.JSONField;

/***
 *
 * Biletinial seat JSON object
 *
 * */
public class BSeat {

    @JSONField(name = "seatName")
    private String seatName;

    @JSONField(name = "seatId")
    private long seatId;

    @JSONField(name = "seatType")
    private String seatType;

    @JSONField(name = "available")
    private boolean available;

    public BSeat() {
        super();
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public long getSeatId() {
        return seatId;
    }

    public void setSeatId(long seatId) {
        this.seatId = seatId;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
