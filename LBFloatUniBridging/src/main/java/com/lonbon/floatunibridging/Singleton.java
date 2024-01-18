package com.lonbon.floatunibridging;

import com.lb.extend.security.broadcast.AreaDivision;

import java.util.ArrayList;

public class Singleton {

    private volatile static Singleton singleton;

    private Singleton (){}

    public static Singleton getSingleton() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }

    private boolean isConnect = false;
    private boolean hasStartExecutor = false;
    private ArrayList<AreaDivision> areaDivisionArrayList = new ArrayList<>();

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public boolean isHasStartExecutor() {
        return hasStartExecutor;
    }

    public void setHasStartExecutor(boolean hasStartExecutor) {
        this.hasStartExecutor = hasStartExecutor;
    }

    public ArrayList<AreaDivision> getAreaDivisionArrayList() {
        return areaDivisionArrayList;
    }

    public void setAreaDivisionArrayList(ArrayList<AreaDivision> areaDivisionArrayList) {
        this.areaDivisionArrayList = areaDivisionArrayList;
    }
}
