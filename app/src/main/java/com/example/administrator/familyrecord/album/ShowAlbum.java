package com.example.administrator.familyrecord.album;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.familyrecord.R;
import com.example.administrator.familyrecord.subHomePage.PhotoFragment;
import com.example.administrator.familyrecord.utils.ConfigUtils;
import com.example.administrator.familyrecord.utils.GetPath;
import com.example.administrator.familyrecord.utils.HttpUtils;
import com.example.administrator.familyrecord.utils.RecyclerViewUtil;
import com.loopj.android.image.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowAlbum extends AppCompatActivity {

    public  JSONObject object;
    private RecyclerView recyclerView;
    public ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
    public RecyclerViewAdapter mAdapter;
    private String id;
    private String type;

    //private ImageView img ;
    private  String srcPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_album);

        FloatingActionButton upload = (FloatingActionButton) findViewById(R.id.btn_upload_photo);
        FloatingActionButton select = (FloatingActionButton) findViewById(R.id.btn_select_photo) ;
        //img = (ImageView) findViewById(R.id.tempimage);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getStringExtra("type");

//        Toast.makeText(ShowAlbum.this, id + " " + type, Toast.LENGTH_SHORT).show();
        init();

        final SwipeRefreshLayout mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.photo_swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                mRefreshLayout.setRefreshing(false);
            }
        });
        RecyclerViewUtil util = new RecyclerViewUtil(this,recyclerView);
        util.setOnItemClickListener(new RecyclerViewUtil.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                //System.out.println(list.get(position));
                //Toast.makeText(getActivity().getApplicationContext(),position+" 单击",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ShowAlbum.this, ShowPhoto.class);
                intent.putExtra("path",list.get(position).get("photo").toString());
                startActivity(intent);
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, 2);

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
                final String creator = sp.getString("username","");


                final JSONObject user = new JSONObject();
                try {
                    user.put("type",type);
                    user.put("creator",creator);
                    user.put("rId",id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Thread(){
                    public void run(){

                        HttpUtils hu = new HttpUtils();
                        String uploadUrl = ConfigUtils.getProperties(getApplicationContext(), "uploadUrl");
                        String url = ConfigUtils.getProperties(getApplicationContext(),"host") + uploadUrl;

                        hu.uploadImage(url,srcPath,user);
                    }

                }.start();



            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){

            Uri uri = data.getData();
            GetPath path = new GetPath();
            srcPath = path.getPath(getApplicationContext(),uri);

        }
    }

    private void init() {
        list.clear();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_photo);
        //使recyclerview保持固定的大小
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置布局管理器，实现横向或数值滚动的列表布局

        Thread getphoto = new Thread(new Runnable() {
            @Override
            public void run() {


                JSONObject album = new JSONObject();
                try {
                    album.put("rId", id);
                    album.put("type",type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String select = ConfigUtils.getProperties(getApplicationContext(), "selectPhotoOrVideoUrl");
                String url = ConfigUtils.getProperties(getApplicationContext(), "host") + select;
                HttpUtils hu = new HttpUtils();
                jsonJX(hu.selectAlbum(url, album));


            }

            private void jsonJX(JSONArray data) {
                //判断数据是空
                if (data != null) {
                    try {
                        //遍历
                        for (int i = 0; i < data.length(); i++) {
                            object = data.getJSONObject(i);

                            Map<String, Object> map = new HashMap<String, Object>();

                            try {
                                //获取到json数据中的内容
                                String photoPath = object.getString("filePath");
                                /*String id = object.getString("id");
                                int type = object.getInt("type");
                                String albumName = object.getString("albumName");*/
                                //存入map
                                map.put("photo", photoPath);
                               /* map.put("albumName", albumName);
                                map.put("id",id);
                                map.put("type",type);*/

                                //ArrayList集合
                                list.add(map);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }

//                        Message message = new Message();
//                        message.what = 1;
//                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
        getphoto.start();
        try {
            getphoto.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mAdapter = new RecyclerViewAdapter(list);
        recyclerView.setAdapter(mAdapter);

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.myViewHolder> {

        private ArrayList data;

        public RecyclerViewAdapter(ArrayList<Map<String, Object>> list) {
            this.data = list;
        }



        @Override
        public myViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            RecyclerViewAdapter.myViewHolder holder= new RecyclerViewAdapter.myViewHolder(View.inflate(viewGroup.getContext(), R.layout.album_item, null));
            return holder;
        }
        //绑定数据


        @Override
        public void  onBindViewHolder(myViewHolder holder, int i) {

            String projectName = ConfigUtils.getProperties(getApplicationContext(), "project");
            String url = ConfigUtils.getProperties(getApplicationContext(),"host") + projectName;
            url += list.get(i).get("photo")==null? "":list.get(i).get("photo").toString();
            holder.albumName.setVisibility(View.INVISIBLE);
            holder.albumCover.setImageUrl(url);


        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        //ViewHolder类
        public class myViewHolder extends RecyclerView.ViewHolder{
            TextView albumName;
            SmartImageView albumCover;
            public myViewHolder(View itemView)
            {
                super(itemView);
                albumName = (TextView)itemView.findViewById(R.id.album_name);
                albumCover = (SmartImageView) itemView.findViewById(R.id.album_cover);
            }
        }
    }
}
