package com.briccsquad.mobileportail;

public class checkGarnier {
    Document infoEcole = Jsoup.connect(urlInfoEcole)
            .post();

    log (infoEcole.span#LNomEcole.text());
    print.ln.out(span#LNomEcole.text)
}
/* FML */
        }