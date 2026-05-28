package com.example.skaitykle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchByTextRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Arrays;
import java.util.List;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    BottomNavigationView bottomNavigationView;
    ChipGroup filterChipGroup;
    Chip chipBookstores, chipLibraries, chipMuseums;
    Chip chipGyms, chipPharmacies;

    private GoogleMap googleMap;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLatLng;

    private static final int LOCATION_PERMISSION_REQUEST = 101;
    private static final int currentUserId = 1;
    private static final double SEARCH_RADIUS = 0.05;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        bottomNavigationView = findViewById(R.id.mapBottomNav);
        bottomNavigationView.setSelectedItemId(R.id.menu_map);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_home) {
                    startActivity(new Intent(getBaseContext(), Title.class));
                    return true;
                } else if (id == R.id.menu_add_book) {
                    startActivity(new Intent(getBaseContext(), AddBook.class));
                    return true;
                }
                else if (id == R.id.menu_library) {
                    startActivity(new Intent(getBaseContext(), Library.class));
                    return true;
                } else if(id == R.id.menu_map){
                    Intent mapIntent = new Intent(getBaseContext(), Map.class);
                    startActivity(mapIntent);
                    return true;
                }else if (id == R.id.menu_profile) {
                    Intent profileIntent = new Intent(getBaseContext(), Profile.class);
                    profileIntent.putExtra("userId", currentUserId);
                    startActivity(profileIntent);
                    return true;
                }
                return false;
            }
        });


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),
                    "AIzaSyBE6mOKFX_eHs3AsdiH8b0-l7CDKyW6z7w");
        }
        placesClient = Places.createClient(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        filterChipGroup = findViewById(R.id.filterChipGroup);
        chipBookstores  = findViewById(R.id.chipBookstores);
        chipLibraries   = findViewById(R.id.chipLibraries);
        chipMuseums     = findViewById(R.id.chipMuseums);
        chipGyms         = findViewById(R.id.chipGyms);
        chipPharmacies   = findViewById(R.id.chipPharmacies);


        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (currentLatLng == null || checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipBookstores) {
                searchNearby("book store");
            } else if (id == R.id.chipLibraries) {
                searchNearby("public library");
            } else if (id == R.id.chipMuseums) {
                searchNearby("museum");
            }
        });

        ChipGroup filterChipGroupBottom = findViewById(R.id.filterChipGroupBottom);
        filterChipGroupBottom.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (currentLatLng == null || checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipGyms) {
                searchNearby("gym");
            } else if (id == R.id.chipPharmacies) {
                searchNearby("pharmacy");
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        requestLocationPermission();
    }


    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            Toast.makeText(this, "Location permission needed to find nearby places",
                    Toast.LENGTH_LONG).show();
        }
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        googleMap.setMyLocationEnabled(true);

        com.google.android.gms.location.LocationRequest locationRequest =
                new com.google.android.gms.location.LocationRequest.Builder(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 3000)
                        .setMinUpdateIntervalMillis(1000)
                        .build();

        com.google.android.gms.location.LocationCallback locationCallback =
                new com.google.android.gms.location.LocationCallback() {
                    @Override
                    public void onLocationResult(
                            @NonNull com.google.android.gms.location.LocationResult result) {
                        android.location.Location location = result.getLastLocation();
                        if (location == null) return;

                        LatLng newLatLng = new LatLng(
                                location.getLatitude(), location.getLongitude());

                        boolean isFirstFix = (currentLatLng == null);
                        currentLatLng = newLatLng;

                        if (isFirstFix) {
                            googleMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f));
                            chipBookstores.setChecked(true);
                        }
                    }
                };

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                android.os.Looper.getMainLooper()
        );
    }


    private void searchNearby(String query) {
        if (currentLatLng == null || googleMap == null) return;

        googleMap.clear();

        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(currentLatLng.latitude  - SEARCH_RADIUS,
                        currentLatLng.longitude - SEARCH_RADIUS),
                new LatLng(currentLatLng.latitude  + SEARCH_RADIUS,
                        currentLatLng.longitude + SEARCH_RADIUS)
        );

        List<Place.Field> fields = Arrays.asList(
                Place.Field.DISPLAY_NAME,
                Place.Field.LOCATION,
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.RATING
        );

        SearchByTextRequest request = SearchByTextRequest.builder(query, fields)
                .setLocationRestriction(bounds)
                .setMaxResultCount(20)
                .build();

        placesClient.searchByText(request)
                .addOnSuccessListener(response -> {
                    List<Place> places = response.getPlaces();
                    if (places.isEmpty()) {
                        Toast.makeText(this, "No results found for: " + query,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float markerColor = getMarkerColor(query);

                    for (Place place : places) {
                        if (place.getLocation() == null) continue;
                        LatLng position = place.getLocation();

                        String snippet = place.getFormattedAddress() != null
                                ? place.getFormattedAddress() : "";
                        if (place.getRating() != null) {
                            snippet += " ★ " + place.getRating();
                        }

                        googleMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(place.getDisplayName())
                                .snippet(snippet)
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("MapSearch", "Search failed: " + e.getMessage(), e);
                    Toast.makeText(this, "Search failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }


    private float getMarkerColor(String query) {
        if (query.contains("book"))    return BitmapDescriptorFactory.HUE_AZURE;
        if (query.contains("library")) return BitmapDescriptorFactory.HUE_GREEN;
        if (query.contains("museum"))  return BitmapDescriptorFactory.HUE_VIOLET;
        if (query.contains("gym"))      return BitmapDescriptorFactory.HUE_ORANGE;
        if (query.contains("pharmacy")) return BitmapDescriptorFactory.HUE_CYAN;
        return BitmapDescriptorFactory.HUE_RED;
    }
}