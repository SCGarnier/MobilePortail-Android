package com.briccsquad.mobileportail;

public class checkGarnier {
    Document infoEcole = Jsoup.connect(urlInfoEcole)
            .post();

    Node nomEcole = infoEcole.select("span#LNomEcole").text();
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