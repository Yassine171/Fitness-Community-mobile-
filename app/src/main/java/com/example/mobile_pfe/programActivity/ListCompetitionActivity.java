package com.example.mobile_pfe.programActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_pfe.Network.RetrofitInstance;
import com.example.mobile_pfe.R;
import com.example.mobile_pfe.UI.CoachContent;
import com.example.mobile_pfe.Adapter.CompetitionAdapter;
import com.example.mobile_pfe.model.Competition.Competition;
import com.example.mobile_pfe.sevices.CompetitionService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCompetitionActivity extends AppCompatActivity {

    private CompetitionAdapter adapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_competition);

        TextView back = findViewById(R.id.Back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListCompetitionActivity.this, CoachContent.class);
                startActivity(intent);

            }
        });

        /*Create handle for the RetrofitInstance interface*/
        CompetitionService service = RetrofitInstance.getRetrofitInstance().create(CompetitionService.class);

        /*Call the method with parameter in the interface to get the employee data*/
        Call<List<Competition>> call = service.getAll();

        /*Log the URL called*/
        Log.wtf("URL Called", call.request().url() + "");

        call.enqueue(new Callback<List<Competition>>() {
            @Override
            public void onResponse(Call<List<Competition>> call, Response<List<Competition>> response) {
                generateEmployeeList((ArrayList<Competition>) response.body());
            }

            @Override
            public void onFailure(Call<List<Competition>> call, Throwable t) {
                Toast.makeText(ListCompetitionActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to UploadActivity when fabAdd is clicked
                Intent intent = new Intent(ListCompetitionActivity.this, AddCompetitionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void generateEmployeeList(ArrayList<Competition> programDataList) {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        if (programDataList == null) {
            // Handle the case where data is null
            // For example, display a message or perform some appropriate action
            Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show();
            return;
        }

        adapter = new CompetitionAdapter(programDataList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListCompetitionActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }
}