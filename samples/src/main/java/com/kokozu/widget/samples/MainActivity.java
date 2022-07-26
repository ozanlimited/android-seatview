package com.kokozu.widget.samples;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.kokozu.widget.seatview.OnChooseSeatListener;
import com.kokozu.widget.seatview.SeatData;
import com.kokozu.widget.seatview.SeatThumbnailView;
import com.kokozu.widget.seatview.SeatView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements OnChooseSeatListener {

    SeatView seatView;
    SeatThumbnailView thumbnailView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seatView = findViewById(R.id.seat_view);
        thumbnailView = findViewById(R.id.thumbnail_view);
        seatView.attachThumbnailView(thumbnailView);
        seatView.setOnChooseSeatListener(this);
        seatView.setSeatState(SeatView.STATE_LOADING);

        loadSeats();

        findViewById(R.id.btn1).setOnClickListener(mRecommendClicked);
        findViewById(R.id.btn2).setOnClickListener(mRecommendClicked);
        findViewById(R.id.btn3).setOnClickListener(mRecommendClicked);
        findViewById(R.id.btn4).setOnClickListener(mRecommendClicked);

        findViewById(R.id.btn5).setOnClickListener(mRegularClicked);
    }

    private View.OnClickListener mRecommendClicked =
            new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int recommendCount;
                    switch (v.getId()) {
                        case R.id.btn1:
                            recommendCount = 1;
                            break;

                        case R.id.btn2:
                            recommendCount = 2;
                            break;

                        case R.id.btn3:
                            recommendCount = 3;
                            break;

                        case R.id.btn4:
                            recommendCount = 4;
                            break;

                        default:
                            recommendCount = 1;
                            break;
                    }
                    List<SeatData> seats = seatView.selectRecommendSeats(recommendCount);
                    if (seats == null || seats.size() == 0) {
                        Toast.makeText(MainActivity.this, "No recommended seats", Toast.LENGTH_SHORT).show();
                    }
                    seatView.setSelectedData(seats);
                }
            };

    private View.OnClickListener mRegularClicked =
            new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean legal = seatView.isSelectedSeatLegal();
                    String message = legal ? "Conform to the rules" : "No empty seats";
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            };

    private void loadSeats() {
        new Thread(
                new Runnable() {

                    @Override
                    public void run() {
                        try {
                            final Pair<String[], List<SeatData>> biletinialList = generateBiletinial();
                            final List<SeatData> seatList = generateSeats();
//                            final List<SeatData> soldSeats = generateSolds();
                            runOnUiThread(
                                    new Runnable() {

                                        @Override
                                        public void run() {
                                            seatView.setSeatData(biletinialList.second,biletinialList.first);
//                                            seatView.setSoldData(soldSeats);
                                        }
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .start();
    }

    private Pair<String[], List<SeatData>> generateBiletinial() {
        try {
            final List<SeatData> seatList = new ArrayList<>();

            InputStream is = getAssets().open("biletinial/seats1.json");
            String seatsText = convertStreamToString(is);
            // https://github.com/alibaba/fastjson/issues/491
            HashMap<String,List<BSeat>> SEATSOFBILETINIAL = (HashMap<String, List<BSeat>>) JSON.parseObject(seatsText, new TypeReference<Map<String, List<BSeat>>>() {});

            Set<String> rowSet = SEATSOFBILETINIAL.keySet();
            String[] rowsArray = new String[rowSet.size()];
            int rowIndex = 1;
            for (String seatRow : rowSet) {
                rowsArray[rowIndex - 1] = seatRow;
                List<BSeat> row = SEATSOFBILETINIAL.get(seatRow);
                boolean drawRightSide = false;
                for (int i = 0; i < row.size(); i++) {
                    SeatData seatData = new SeatData();
                    if (row.get(i).getSeatType().equals("SEALED") || row.get(i).getSeatType().equals("LETTER")) {
                        // boşluk
                        continue;
                    }
                    seatData.state = row.get(i).isAvailable()
                                    ? SeatData.STATE_NORMAL
                                    : SeatData.STATE_SOLD;
                    seatData.seatNo = seatRow + "" + (i+1);
                    seatData.point = new Point(rowIndex, i+1);
                    if (row.get(i).getSeatType().equals("HANDICAPPED")) {
                        seatData.type = SeatData.TYPE_AFFLICTED;
                    } else if (row.get(i).getSeatType().equals("DOUBLE")) {
                        // çift koltuk
                        if (drawRightSide) {
                            // koltuğun sağ tarafı
                            seatData.type = SeatData.TYPE_LOVER_RIGHT;
                            drawRightSide = false;
                        } else {
                            // koltuğun sol tarafı
                            seatData.type = SeatData.TYPE_LOVER_LEFT;
                            drawRightSide = true;
                        }
                    } else {
                        seatData.type = SeatData.TYPE_NORMAL;
                    }
                    seatList.add(seatData);
                }
                rowIndex = rowIndex+1;
            }
            return new Pair(rowsArray, seatList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<SeatData> generateSeats() {
        try {
            InputStream is = getAssets().open("seats.json");
            String seatsText = convertStreamToString(is);
            JSONObject object = JSON.parseObject(seatsText);
            final List<Seat> seats =
                    JSON.parseArray(object.getString("seats"), Seat.class);

            if (seats != null) {
                final List<SeatData> seatList = new ArrayList<>();
                for (Seat seat : seats) {
                    SeatData seatData = new SeatData();
                    seatData.state =
                            seat.getSeatState() == 0
                                    ? SeatData.STATE_NORMAL
                                    : SeatData.STATE_SOLD;
                    seatData.point =
                            new Point(
                                    seat.getGraphRow(), seat.getGraphCol());
                    if (seat.getSeatType() == 1) {
                        seatData.type =
                                seat.isLoverL()
                                        ? SeatData.TYPE_LOVER_LEFT
                                        : SeatData.TYPE_LOVER_RIGHT;
                    } else {
                        seatData.type = seat.isAfflicted() ? SeatData.TYPE_AFFLICTED: SeatData.TYPE_NORMAL;
                    }
                    seatList.add(seatData);
                }
                return seatList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<SeatData> generateSolds() {
        try {
            InputStream is = getAssets().open("sold_seat.json");
            String seatsText = convertStreamToString(is);
            JSONObject object = JSON.parseObject(seatsText);
            List<Seat> seats =
                    JSON.parseArray(object.getString("seats"), Seat.class);

            if (seats != null) {
                final List<SeatData> seatList = new ArrayList<>();
                for (Seat seat : seats) {
                    SeatData seatData = new SeatData();
                    seatData.state =
                            seat.getSeatState() == 0
                                    ? SeatData.STATE_NORMAL
                                    : SeatData.STATE_SOLD;
                    seatData.point =
                            new Point(
                                    seat.getGraphRow(), seat.getGraphCol());
                    if (seat.getSeatType() == 1) {
                        seatData.type =
                                seat.isLoverL()
                                        ? SeatData.TYPE_LOVER_LEFT
                                        : SeatData.TYPE_LOVER_RIGHT;
                    } else {
                        seatData.type = seat.isAfflicted() ? SeatData.TYPE_AFFLICTED: SeatData.TYPE_NORMAL;
                    }
                    seatList.add(seatData);
                }

                return seatList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    @Override
    public void onPickLoverSeatOverMaxCount(int maxSelectCount) {
        Log.i("MainActivity","onPickLvrSOverMaxCount, Couple seat exceeds seat limit");
    }

    @Override
    public void onSelectedSeatOverMaxCount(int maxSelectCount) {
        Log.i("MainActivity","onSelectedSeOverMxCount most choice" + maxSelectCount + "seats");
    }

    @Override
    public void onSelectSeatNotMatchRegular() {
        Log.i("MainActivity","onSelectSNtMatchRegular Can't leave empty seat");
    }

    @Override
    public void onSelectedSeatChanged(List<SeatData> selectedSeats) {
        if (selectedSeats == null) {
            return;
        }
        if (selectedSeats.isEmpty()) {
            return;
        }

        StringBuilder seats = new StringBuilder();
        for (SeatData seat : selectedSeats) {
            seats.append(seat.point.x);
            seats.append("-");
            seats.append(seat.point.y);
            seats.append(", ");
        }
        Log.d("MainActivity","onSelectedSeatChanged Seat selected： " + seats);
    }

    @Override
    public void onSelectedSeatSold() {
        Log.i("MainActivity", "onSelectedSeatSold The selected seat has already been sold");
    }
}
