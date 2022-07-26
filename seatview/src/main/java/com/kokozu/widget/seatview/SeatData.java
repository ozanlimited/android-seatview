package com.kokozu.widget.seatview;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * seat。
 *
 * @author wuzhen
 * @since 2017-04-20
 */
public class SeatData implements Parcelable {

    /**
     * optional seat。
     */
    public static final int STATE_NORMAL = 0;

    /**
     * Seats sold。
     */
    public static final int STATE_SOLD = 1;

    /**
     * selected seat。
     */
    public static final int STATE_SELECTED = 2;

    /**
     * normal seat。
     */
    public static final int TYPE_NORMAL = 0;

    /**
     * The left seat of the couple's seat。
     */
    public static final int TYPE_LOVER_LEFT = 1;

    /**
     * The right side of the couple's seat。
     */
    public static final int TYPE_LOVER_RIGHT = 2;

    /**
     * Afflicted seat。
     */
    public static final int TYPE_AFFLICTED = 3;

    /**
     * the coordinates of the seat。
     */
    public Point point;

    /**
     * seat status。
     */
    @SeatState
    public int state;

    /**
     * type of seat。
     */
    @SeatType
    public int type;

    public String seatRow;

    public String seatCol;

    public String seatNo;

    public String pieceNo;

    public String extra;

    /**
     * Determine whether it is a couple。
     *
     * @return Is it a couple
     */
    boolean isLoverSeat() {
        return type == TYPE_LOVER_LEFT || type == TYPE_LOVER_RIGHT;
    }
    boolean isAfflictedSeat() {
        return type == TYPE_AFFLICTED;
    }

    /**
     * Determine whether it is the seat on the left of the couple's seat。
     *
     * @return Is it the left seat of the couple's seat?
     */
    boolean isLoverLeftSeat() {
        return type == TYPE_LOVER_LEFT;
    }

    /**
     * Determine whether it is the seat on the right side of the couple's seat。
     *
     * @return Is the seat on the right side of the couple's seat?
     */
    boolean isLoverRightSeat() {
        return type == TYPE_LOVER_RIGHT;
    }

    /**
     * select seat。
     *
     * @return Whether the selection is successful
     */
    boolean selectSeat() {
        if (state == STATE_NORMAL) {
            state = STATE_SELECTED;
            return true;
        }
        return false;
    }

    /**
     * unchecked seat。
     *
     * @return Whether the cancellation was successful
     */
    boolean unSelectSeat() {
        if (state == STATE_SELECTED) {
            state = STATE_NORMAL;
            return true;
        }
        return false;
    }

    String seatKey() {
        return point.x + "-" + point.y;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_NORMAL, STATE_SOLD, STATE_SELECTED})
    public @interface SeatState {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_NORMAL, TYPE_LOVER_LEFT, TYPE_LOVER_RIGHT, TYPE_AFFLICTED})
    public @interface SeatType {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.point, flags);
        dest.writeInt(this.state);
        dest.writeInt(this.type);
        dest.writeString(this.seatRow);
        dest.writeString(this.seatCol);
        dest.writeString(this.seatNo);
        dest.writeString(this.pieceNo);
        dest.writeString(this.extra);
    }

    public SeatData() {
    }

    protected SeatData(Parcel in) {
        this.point = in.readParcelable(Point.class.getClassLoader());
        this.state = in.readInt();
        this.type = in.readInt();
        this.seatRow = in.readString();
        this.seatCol = in.readString();
        this.seatNo = in.readString();
        this.pieceNo = in.readString();
        this.extra = in.readString();
    }

    public static final Creator<SeatData> CREATOR =
            new Creator<SeatData>() {

                @Override
                public SeatData createFromParcel(Parcel source) {
                    return new SeatData(source);
                }

                @Override
                public SeatData[] newArray(int size) {
                    return new SeatData[size];
                }
            };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SeatData seatData = (SeatData) o;

        if (point != null ? !point.equals(seatData.point) : seatData.point != null) {
            return false;
        }
        if (seatRow != null ? !seatRow.equals(seatData.seatRow) : seatData.seatRow != null) {
            return false;
        }
        if (seatCol != null ? !seatCol.equals(seatData.seatCol) : seatData.seatCol != null) {
            return false;
        }
        return seatNo != null ? seatNo.equals(seatData.seatNo) : seatData.seatNo == null;
    }

    @Override
    public int hashCode() {
        int result = point != null ? point.hashCode() : 0;
        result = 31 * result + (seatRow != null ? seatRow.hashCode() : 0);
        result = 31 * result + (seatCol != null ? seatCol.hashCode() : 0);
        result = 31 * result + (seatNo != null ? seatNo.hashCode() : 0);
        return result;
    }
}
