package com.briccsquad.mobileportail;

public class checkGarnier {
    Document infoEcole = Jsoup.connect(urlInfoEcole)
            .post();

    Element nomEcole = infoEcole.select("span#LNomEcole.text").text();
    if (nomEcole.text()=="ÉSC Saint-Charles-Garnier - Whitby")
        {
            //code for true
        }
        else{
            //code for false
        };

}
/* FML */
        }