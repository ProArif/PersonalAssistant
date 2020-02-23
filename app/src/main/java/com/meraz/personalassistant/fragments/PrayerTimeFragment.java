package com.meraz.personalassistant.fragments;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.meraz.personalassistant.AppController;
import com.meraz.personalassistant.R;
import com.meraz.personalassistant.activities.LoginActivity;
import com.meraz.personalassistant.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class PrayerTimeFragment extends Fragment {

    private String url;
    String tag_json_obj = "json_obj_req";
    AlertDialog pDialog;
    private Context context;
    private TextView tvFazr, tvDhuhr, tvAsr, tvMaghrib, tvIsha, tvLoc;
    private EditText edt_search;
    private String mFazr, mDhuhr, mAsr, mMaghrib, mIsha, mLocation, mCountry, mState, mCity, loc;
    private Button btn_search;
    private static final String TAG = "tag";

    private LocationManager locationManager;
    private double lat, lng;
    private Location location;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    public PrayerTimeFragment() {
        // Required empty public constructor
    }


    public static PrayerTimeFragment newInstance(String param1, String param2) {
        PrayerTimeFragment fragment = new PrayerTimeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        context = getContext();
        setHasOptionsMenu(true);

//        locationManager = (LocationManager) (context != null ? context
//                .getSystemService(Context.LOCATION_SERVICE) : null);
//        if ( ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
//
//            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION},
//                    1 );
//        }
//        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        mLocation = "riyadh";
        url = "https://muslimsalat.com/"+mLocation+".json?key=f8b1de686f9d4bfa0fcf8ecb6b7adad3";
        searchLoc();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_prayer, container, false);
        tvFazr = view.findViewById(R.id.fazr);
        tvDhuhr = view.findViewById(R.id.zohr);
        tvAsr = view.findViewById(R.id.achr);
        tvMaghrib = view.findViewById(R.id.magrib);
        tvIsha = view.findViewById(R.id.esa);
        tvLoc = view.findViewById(R.id.loc);
        edt_search = view.findViewById(R.id.edtCity);
        btn_search = view.findViewById(R.id.btnSearch);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocation = edt_search.getText().toString();
                if (mLocation.isEmpty()){
                    edt_search.setError("Please Enter Your City!");
                    Toast.makeText(context,"Please Enter Your City",Toast.LENGTH_LONG).show();
                } else {
                    url = "https://muslimsalat.com/"+mLocation+".json?key=f8b1de686f9d4bfa0fcf8ecb6b7adad3";
                    searchLoc();
                }
            }
        });


        return view;
    }




    private void searchLoc() {
//        pDialog.setMessage("Loading...");
//        pDialog.show();
        Log.e("entered", "entered search loc");


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Get Data from API
                        try {
                            Log.e("entered", "entered try block");

                            //get location
                            mCountry = response.get("country").toString();
                            mCity = response.get("city").toString();
                            mState = response.get("state").toString();
                            loc =  mCountry;
                            mFazr = response.getJSONArray("items")
                                    .getJSONObject(0).get("fajr").toString();
                            mDhuhr = response.getJSONArray("items")
                                    .getJSONObject(0).get("dhuhr").toString();
                            mAsr = response.getJSONArray("items")
                                    .getJSONObject(0).get("asr").toString();
                            mMaghrib = response.getJSONArray("items")
                                    .getJSONObject(0).get("maghrib").toString();
                            mIsha = response.getJSONArray("items")
                                    .getJSONObject(0).get("isha").toString();
                            Log.e("fazr time",mFazr);

                            tvFazr.setText(mFazr);
                            tvDhuhr.setText(mDhuhr);
                            tvAsr.setText(mAsr);
                            tvMaghrib.setText(mMaghrib);
                            tvIsha.setText(mIsha);
                            tvLoc.setText(loc);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, response.toString());
//                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
//                pDialog.hide();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq,tag_json_obj);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.logout,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder1.setMessage("Do you want to Logout?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (user != null){
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("credentials", MODE_PRIVATE).edit();

                            clearStack();
                            Intent n = new Intent(getActivity(), LoginActivity.class);
                            n.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            editor.clear();
                            editor.apply();
                            startActivity(n);
                            mAuth.signOut();
                            //finish();
                        }

                        dialog.cancel();
                        //startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        androidx.appcompat.app.AlertDialog alert11 = builder1.create();
        alert11.show();

        return super.onOptionsItemSelected(item);
    }

    public void clearStack() {
        //Here we are clearing back stack fragment entries
        int backStackEntry = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        }

        //Here we are removing all the fragment that are shown here
        if (getActivity().getSupportFragmentManager().getFragments() != null && getActivity().getSupportFragmentManager().getFragments().size() > 0) {
            for (int i = 0; i < getActivity().getSupportFragmentManager().getFragments().size(); i++) {
                Fragment mFragment = getActivity().getSupportFragmentManager().getFragments().get(i);
                if (mFragment != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
                }
            }
        }
    }
}
