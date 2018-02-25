package com.briccsquad.mobileportail;

import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.util.ArrayList;

public class NewsActivity {

    public void testBus() {

        // Get bus availability
        Document siteGarnier = null;
        try {
            siteGarnier = Jsoup.connect("")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // School state (open, closed) and buses use the same class name ----- We need to parse into a list
        // Can we replace Node with array somehow?

        String autobusGarnier = siteGarnier.select("li.list-group-item li.list-group-item-success").text();


        //String ecoleOuverte = autobusGarnier("premier element de la liste");

        //String transportG = autobusGarnier("deuxième element de la liste");

        // A list that should hold "École Ouverte/Fermée" in first position and "Transport normal/perturbé/annulé" in second position
        // To be imported into the layout and displayed in the 'Nouvelles' tab

        ArrayList schoolBusState = new ArrayList<>();
        //schoolBusState.add(ecoleOuverte);
        //schoolBusState.add(transportG);
    }
    /*public void displayBusState {
        // Read method name
        newtext = (TextView) findViewById(R.id.);
    }*/
}