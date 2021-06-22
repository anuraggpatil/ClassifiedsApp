package com.phoenixcorp.classifiedsapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_home,container,false);

        RecyclerView feed=view.findViewById(R.id.feed);



        ArrayList<String> productsFromDB=new ArrayList<>();
        ArrayList<String> priceFromDB=new ArrayList<>();
        ArrayList<String> UIDFromDB=new ArrayList<>();
        HashMap<String,String> names=new HashMap<>();

        HashMap<String,String> imageURLFromDB=new HashMap<>();


        FeedListAdapter adapter;

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        productsFromDB.add(document.getString("productName"));
                        priceFromDB.add(document.getString("price"));
                        UIDFromDB.add(document.getString("UID"));

                        db.collection("posts/"+document.getId()+"/urls").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                List<DocumentSnapshot> documentList=task.getResult().getDocuments();



                                for(int i=0;i<1;i++){
//                                    imageURLFromDB.add(documentList.get(i).getString("url"));
                                    imageURLFromDB.put(document.getString("productName"),documentList.get(i).getString("url"));
                                }

                                adapterHandler(productsFromDB,priceFromDB,imageURLFromDB,UIDFromDB,names,feed);
                                feed.setHasFixedSize(true);

                            }
                        });
                        Log.d("products", "onCreateView: images form db "+imageURLFromDB);
                    }
//                    adapter=new FeedListAdapter(productsFromDB,prices);


                    Log.d("products", "onComplete:106 "+productsFromDB);

                }else{
                    Toast.makeText(getContext(),"Couldn't Fetch",Toast.LENGTH_LONG).show();
                }
            }


        });

        for(String uid:UIDFromDB) {
            db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    DocumentSnapshot user=task.getResult();
                    String name=user.getString("username");

                    names.put(uid,name);


                }
            });
        }




        feed.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
//        feed.setAdapter(adapter);









        return view;
    }

    private void adapterHandler(ArrayList<String> products,ArrayList<String> prices, HashMap<String,String> imagesURLs, ArrayList<String> UIDs,HashMap<String,String> names,RecyclerView feed) {
        FeedListAdapter adapter=new FeedListAdapter(products,prices,imagesURLs,UIDs,names,this);
        if(imagesURLs.size()!=products.size() && names.size()!=products.size()){
            return;
        }else{
            Log.d("products", "onCreateView: "+imagesURLs);
            feed.setAdapter(adapter);
        }

        Log.d("images", "adapterHandler: at 131"+imagesURLs.size());
    }


}