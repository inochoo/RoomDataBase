package com.sibaamap.roomdatabase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sibaamap.roomdatabase.database.UserDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 123;
    private EditText edtUserName;
    private EditText edtAddress;
    private EditText edtSearch;
    private Button btnAddUser;
    private TextView tvDeleteAll;
    private RecyclerView rcvUser;

    private UserAdapter userAdapter;
    private List<User> mListUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();

        userAdapter = new UserAdapter(new UserAdapter.IClickItemUser() {
            @Override
            public void updateUser(User user) {
                clickUpdateUser(user);
            }

            @Override
            public void deleteUser(User user) {
                clickDeleteUser(user);

            }
        });
        mListUser = new ArrayList<>();
        userAdapter.setData(mListUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvUser.setLayoutManager(linearLayoutManager);
        rcvUser.setAdapter(userAdapter);


        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
        tvDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDeleteAllUser();

            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    //logic search
                    handleSearchUser();
                }
                return false;
            }
        });
        loadData();
    }

    private void handleSearchUser() {
        String strKeyword = edtSearch.getText().toString().trim();
        mListUser = new ArrayList<>();
        mListUser = UserDatabase.getInstance(this).userDao().searchUser(strKeyword);
        userAdapter.setData(mListUser);
        hideSoftKeyboard();
    }

    private void addUser() {
        String strUserName = edtUserName.getText().toString().trim();
        String strAddress = edtAddress.getText().toString().trim();

        if(TextUtils.isEmpty(strUserName) || TextUtils.isEmpty(strAddress)){
            return;
        }

        User user = new User(strUserName,strAddress);

        if(isUserExits(user)){
            Toast.makeText(this, "User exist", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDatabase.getInstance(this).userDao().insertUser(user);
        Toast.makeText(this, "Add user successfully", Toast.LENGTH_SHORT).show();

        edtUserName.setText("");
        edtAddress.setText("");
        // ẩn bàn phím
        hideSoftKeyboard();
        loadData();


    }
    public void hideSoftKeyboard(){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    private void initUi() {
        edtUserName = findViewById(R.id.edt_username);
        edtAddress = findViewById(R.id.edt_address);
        btnAddUser = findViewById(R.id.btn_add_user);
        rcvUser = findViewById(R.id.rcv_user);
        tvDeleteAll = findViewById(R.id.tv_delete_all);
        edtSearch = findViewById(R.id.edt_search);
    }
    private void loadData(){
        mListUser = UserDatabase.getInstance(this).userDao().getListUser();
        userAdapter.setData(mListUser);
    }
    private boolean isUserExits(User user){
        List<User> list = UserDatabase.getInstance(this).userDao().checkUser(user.getUsername());
        return  list!=null && !list.isEmpty();
    }
    private void clickUpdateUser(User user) {
        Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_user",user);
        intent.putExtras(bundle);
        startActivityForResult(intent,MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_REQUEST_CODE && requestCode == Activity.RESULT_OK){
            loadData();
        }
    }
    private void clickDeleteUser(User user){
        new AlertDialog.Builder(this)
                .setTitle("Confirm delete user")
                .setMessage("Are you sure")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete user
                        UserDatabase.getInstance(MainActivity.this).userDao().deleteUser(user);
                        Toast.makeText(MainActivity.this,"Delete user successfully",Toast.LENGTH_SHORT).show();

                        loadData();
                    }
                })
                .setNegativeButton("No",null)
                .show();

    }
    private void clickDeleteAllUser() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm delete all user")
                .setMessage("Are you sure")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete user
                        UserDatabase.getInstance(MainActivity.this).userDao().deleteAllUser();
                        Toast.makeText(MainActivity.this,"Delete all user successfully",Toast.LENGTH_SHORT).show();

                        loadData();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }
}