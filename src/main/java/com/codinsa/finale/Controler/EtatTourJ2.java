package com.codinsa.finale.Controler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EtatTourJ2 extends EtatDefaut {

    final Logger log = LoggerFactory.getLogger(getClass());



    @Override
    public String getState(){
        return "TourJ2";
    }
}
