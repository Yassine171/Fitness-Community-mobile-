package com.example.mobile_pfe.GroupActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mobile_pfe.GroupActivity.ListAdapter;
import com.example.mobile_pfe.GroupActivity.User;
import com.example.mobile_pfe.Network.RetrofitInstance;
import com.example.mobile_pfe.R;
import com.example.mobile_pfe.databinding.ActivityListvieweachgroupBinding;
import com.example.mobile_pfe.Model.Globals.AppGlobals;
import com.example.mobile_pfe.model.GroupDTO;
import com.example.mobile_pfe.model.Members;
import com.example.mobile_pfe.model.Sportif;
import com.example.mobile_pfe.sevices.GroupService;
import com.example.mobile_pfe.sevices.SportifService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemActivity extends AppCompatActivity {
    ActivityListvieweachgroupBinding Binding;
    List<GroupDTO> groups;
    GroupDTO UpdateGroupDTO;
    String[] name;
    int selectedGroupID;
    private static final String TAG = "ItemActivity";
    private List<Sportif> sportifs;
    private String email ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = ActivityListvieweachgroupBinding.inflate(getLayoutInflater());
        setContentView(Binding.getRoot());

        try {
            groups = (List<GroupDTO>) getIntent().getSerializableExtra("GROUPS");
            int position = getIntent().getIntExtra("TEAM_INDEX", -1);
            //Toast.makeText(this, "position: " + position, Toast.LENGTH_SHORT).show();

            if (groups == null || position < 0 || position >= groups.size()) {
                Log.e(TAG, "Invalid data received in Intent");
                // Handle the case where data is missing or invalid
                // You can show an error message and finish the activity
                finish();
                return;
            }

            GroupDTO selectedGroup = groups.get(position);
            Binding.groupA.setText(selectedGroup.getName());
            selectedGroupID =groups.get(position).getId().intValue();

            // Check if the selected group has members
            if (selectedGroup != null && selectedGroup.getMembers() != null) {
                List<Members> membersList = selectedGroup.getMembers();

                // Create an array to store member names
                name = new String[membersList.size()];

                // Iterate through the members and extract their names
                for (int i = 0; i < membersList.size(); i++) {
                    Members member = membersList.get(i);
                    // Assuming that members have firstName and lastName fields
                    String fullName = member.getFirstName() + " " + member.getLastName();
                    name[i] = fullName;
                }

                // Now, the 'name' array contains the names of the members in the selected group
            } else {
                Log.e(TAG, "Selected group has no members");
                // Handle the case where the selected group has no members
            }

        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
            // Handle any unexpected exceptions
            // You can show an error message and finish the activity
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

        int[] imageId = {R.drawable.d, R.drawable.d, R.drawable.e,
                R.drawable.g, R.drawable.m, R.drawable.a};
        Random random = new Random();

        ArrayList<User> userArrayList = new ArrayList<>();
        for (int i = 0; i < name.length; i++) {
            User user = new User(name[i], imageId[random.nextInt(imageId.length)], i);
            userArrayList.add(user);
        }

        com.example.mobile_pfe.GroupActivity.ListAdapter listAdapter = new ListAdapter(ItemActivity.this, userArrayList);

        Binding.lisvieww.setAdapter(listAdapter);
        Binding.lisvieww.setClickable(true);
        Button joinButton = findViewById(R.id.button2);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGroup(selectedGroupID);
                finish();
            }
        });
        Button cancelButton = findViewById(R.id.button3);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void updateGroup(int selectedGroupID) {
        GroupService groupService = RetrofitInstance.getRetrofitInstance().create(GroupService.class);
        fetchSportifs();

        Call<GroupDTO> call = groupService.updateGroup((long) selectedGroupID,UpdateGroupDTO);

        call.enqueue(new Callback<GroupDTO>() {
            @Override
            public void onResponse(Call<GroupDTO> call, Response<GroupDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GroupDTO group = response.body();
                    Log.d("Group", "Group updated successfully: " + group.getName());
                    Toast.makeText(ItemActivity.this, "Group updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ItemActivity.this, "Failed to update group", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GroupDTO> call, Throwable t) {
                Log.e("Error", "onFailure Network error: " + t.getMessage());
                Toast.makeText(ItemActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchSportifs() {
        SportifService service = RetrofitInstance.getRetrofitInstance().create(SportifService.class);
        Call<List<Sportif>> call = service.getAllSportifs();

        call.enqueue(new Callback<List<Sportif>>() {
            @Override
            public void onResponse(Call<List<Sportif>> call, Response<List<Sportif>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    findSportifbyEmail(response.body());
                } else {
                    Toast.makeText(ItemActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Sportif>> call, Throwable t) {
                Toast.makeText(ItemActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void findSportifbyEmail(List<Sportif> sportifs) {
        this.sportifs = sportifs; // Store the sportifs for later use
        email = AppGlobals.getEmail();

        //using the selectedGroupID on groups get the selected group
        for (GroupDTO group : groups) {
            if (group.getId().intValue() == selectedGroupID) {
                UpdateGroupDTO = group;
                //get all the members and add to them sportif
                List<Members> membersList = group.getMembers();
                for (Sportif sportif : sportifs) {
                    if (sportif.getEmail().equals(email)) {
                        Members member = new Members();
                        member.setFirstName(sportif.getFirstName());
                        member.setLastName(sportif.getLastName());
                        member.setEmail(sportif.getEmail());
                        member.setId(sportif.getId());
                        member.setAddress(sportif.getAddress());
                        member.setAge(sportif.getAge());
                        member.setPoids(sportif.getPoids());
                        member.setTaille(sportif.getTaille());
                        member.setPicturePath(sportif.getPicturePath());
                        membersList.add(member);
                    }
                }
                //now insert the new memberslist to the group
                UpdateGroupDTO.setMembers(membersList);
            }
        }





    }
}
