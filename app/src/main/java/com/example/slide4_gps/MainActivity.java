package com.example.slide4_gps;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;

public class MainActivity extends AppCompatActivity {
    // chỉ hỗ trợ máy có cài google play services = CH PLay
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnCheck).setOnClickListener(view -> {

            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo infoWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo info3G = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (infoWifi.isConnected())
                Toast.makeText(this, "Bạn đang dùng wifi", Toast.LENGTH_SHORT).show();
            if (info3G.isConnected())
                Toast.makeText(this, "Bạn đang xài 3G", Toast.LENGTH_SHORT).show();


        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ActivityResultLauncher<String[]> locationPermissionRequest =
                    registerForActivityResult(new ActivityResultContracts
                                    .RequestMultiplePermissions(), result -> {
                                Boolean fineLocationGranted = null;
                                fineLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                                Boolean coarseLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION, false);
                                if (fineLocationGranted != null && fineLocationGranted) {
                                    // Precise location access granted.
                                    fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                                        @Override
                                        public boolean isCancellationRequested() {
                                            return false;
                                        }

                                        @NonNull
                                        @Override
                                        public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                                            return null;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            Toast.makeText(MainActivity.this, location.getLatitude() + " : " +
                                                    location.getLongitude(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                    // Only approximate location access granted.
                                    fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            Toast.makeText(MainActivity.this, location.getLatitude() + " : " +
                                                    location.getLongitude(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    // No location access granted.
                                    Toast.makeText(this, "App cần được cấp quyền mới hiển thị GPS được!!!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                    );
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else { // xu ly xin quyen location cho android 5 6
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 999);
            } else {

            }

        }
    }

}