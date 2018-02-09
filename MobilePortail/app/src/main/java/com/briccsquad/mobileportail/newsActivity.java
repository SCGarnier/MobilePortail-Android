package com.briccsquad.mobileportail;

public class newsActivity {

    public void testBus {

        // Get bus availability
        Document siteGarnier = Jsoup.connect(urlSiteGarnier)
                .get();
        // School state (open, closed) and buses use the same class name ----- We need to parse into a list
        // Can we replace Node with array somehow?

        Node autobusGarnier = siteGarnier.select("li.list-group-item list-group-item-success").text();


        String ecoleOuverte;
        ecoleOuverte = autobusGarnier("premier element de la liste");

        String transportG;
        transportG = autobusGarnier("deuxième element de la liste");

        // A list that should hold "École Ouverte/Fermée" in first position and "Transport normal/perturbé/annulé" in second position
        // To be imported into the layout and displayed in the 'Nouvelles' tab

        new Arraylist = Arraylist<String> schoolBusState;
            schoolBusState.add(ecoleOuverte);
            schoolBusState.add(transportG);

    }
    public void displayBusState {
        // Read method name
        newtext = (TextView) findViewById(R.id.);
    }

}