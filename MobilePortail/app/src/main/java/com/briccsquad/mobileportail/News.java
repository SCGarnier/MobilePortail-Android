package com.briccsquad.mobileportail;

public class checkGarnier {
<<<<<<< HEAD
    Document infoEcole = Jsoup.connect(urlInfoEcole)
            .post();

    Element nomEcole = infoEcole.select("span#LNomEcole.text").text();
    if (nomEcole.text()=="ÉSC Saint-Charles-Garnier - Whitby")
        {
=======
    private static void {
        Document infoEcole = Jsoup.connect(urlInfoEcole)
                .post();
        
        Node nomEcole = infoEcole.select("span#LNomEcole").text();
        if (nomEcole.text()=="ÉSC Saint-Charles-Garnier - Whitby")
            {
>>>>>>> 262c628e0e7b951ea8e966ac73719f2667e65654
            //code for true
            }
            else{
            //code for false
            };

    }

}
/* FML */
        }
