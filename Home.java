package com.example.myworkpss;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import Glid.GlideApp;
import Glid.GlideToVectorYou;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private String IMAGE_URL = "http://www.clker.com/cliparts/u/Z/2/b/a/6/android-toy-h.svg";

    // TODO: Rename and change types of parameters
    private String mParam1;

    ArrayList<MenuKeyItem> mCustomKeyList ;
    ConstraintLayout mCustomKey1,mCustomKey2, mCustomKey3, mCustomKey4,mDragView, mContainer;
    ImageView mView;
    MotionLayout mainLayoutCustom;
    private static FragmentManager mFragmentManager;
    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, FragmentManager fragmentManager) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        mFragmentManager = fragmentManager;
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        setupKeyArray();
        setupKey();
        EditMode();
        GlideToVectorYou
                .init()
                .with(getContext())
                .withListener(new GlideToVectorYou.GlideToVectorYouListener() {
                    @Override
                    public void onLoadFailed() {
                        Toast.makeText(getContext(), "Load failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResourceReady() {
                        Toast.makeText(getContext(), "Image ready", Toast.LENGTH_SHORT).show();
                    }
                })
                .load(Uri.parse(IMAGE_URL), mView);

        mainLayoutCustom.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                if(i == R.id.motion_left){
                    Toast.makeText(getActivity(),"toLeft",Toast.LENGTH_SHORT).show();
                    openFragment(Left.newInstance("LEFT"));
                }
                if(i == R.id.motion_center){
                    Toast.makeText(getActivity(),"toCenter",Toast.LENGTH_SHORT).show();
                }
                if(i == R.id.motion_right){
                    Toast.makeText(getActivity(),"toRight",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

            }
        });
        return view;

    }
    private void init(View view){
        mCustomKeyList = new ArrayList<>();
        mainLayoutCustom = view.findViewById(R.id.mainLayout);
        mCustomKey1 =  view.findViewById(R.id.custom_key_1);
        mCustomKey2 =  view.findViewById(R.id.custom_key_2);
        mCustomKey3 =  view.findViewById(R.id.custom_key_3);
        mCustomKey4 =  view.findViewById(R.id.custom_key_4);
        mDragView =  view.findViewById(R.id.drag_view);
        mContainer = view.findViewById(R.id.containers);
        mView =  view.findViewById(R.id.imageView);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();;
        fragmentTransaction.replace(mContainer.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }
    private void setupKeyArray(){
        MenuKeyItem key1 = new MenuKeyItem(1,R.drawable.phone);
        MenuKeyItem key2 = new MenuKeyItem(2,R.drawable.navi);
        MenuKeyItem key3 = new MenuKeyItem(3,R.drawable.setting);
        MenuKeyItem key4 = new MenuKeyItem(4,R.drawable.spotify);
        mCustomKeyList.add(key1);
        mCustomKeyList.add(key2);
        mCustomKeyList.add(key3);
        mCustomKeyList.add(key4);
        //mainLayoutCustom.setCustomKeyList(mCustomKeyList);
    }

    private void setupKey() {
        for (int i = 0; i < mCustomKeyList.size(); i++) {
            int keyId = getContext().getResources().getIdentifier("custom_key_" + String.valueOf(i + 1), "id", getContext().getPackageName());
            ConstraintLayout Key =  mainLayoutCustom.findViewById(keyId);
            ImageView backgr = Key.findViewById(R.id.icon);
            backgr.setBackground(getResources().getDrawable(mCustomKeyList.get(i).getDrawbleImage()));
        }
    }


    private void EditMode(){
        mCustomKey1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EventBus.getDefault().post(new CustomKeyEditModeEvent(mCustomKeyList.get(0)));
                return false;
            }
        });
        mCustomKey2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EventBus.getDefault().post(new CustomKeyEditModeEvent(mCustomKeyList.get(1)));
                return false;
            }
        });
        mCustomKey3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EventBus.getDefault().post(new CustomKeyEditModeEvent(mCustomKeyList.get(2)));
                return false;
            }
        });
        mCustomKey4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EventBus.getDefault().post(new CustomKeyEditModeEvent(mCustomKeyList.get(3)));
                return false;
            }
        });
    }
   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditModeEvent(CustomKeyEditModeEvent customKeyEditModeEvent) {
        mainLayoutCustom.startCustomKeyEditMode(customKeyEditModeEvent);
    }*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotoHome(Home homeFragment) {
        mainLayoutCustom.transitionToStart();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}