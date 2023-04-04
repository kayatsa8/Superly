package Presentation.CLIPresentation.Screens.InventoryScreens;


import Presentation.CLIPresentation.Screens.Screen;

import java.util.Arrays;
import java.util.stream.Stream;

public class Reports extends Screen {

    private static final String[] menuOptions = {
            "View Stock Reports",  //1
            "View Defective Item Reports",                  //2
            "Report Defective Items",          //3
            //"View History of purchases from suppliers",      //4
            "exit" //4
    };

    public Reports(Screen caller, String[] extraMenuOptions) {
        super(caller, Stream.concat(Arrays.stream(menuOptions), Arrays.stream(extraMenuOptions)).toArray(String[]::new));
    }

    public void run() {
        System.out.println("\nWelcome to the Reports Menu");
        switch (runMenu()) {
            case 1:
                new Thread(new StockReports(this, new String[]{})).start();
                break;
            case 2:
                new Thread(new DefectiveItemReports(this, new String[]{})).start();
                break;
            case 3:
                new Thread(new ReportDefective(this, new String[]{})).start();
                break;
            case 4:
                endRun();
        }
    }
}
