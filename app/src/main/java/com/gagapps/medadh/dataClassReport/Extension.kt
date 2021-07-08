package com.gagapps.medadh.dataClassReport

import com.gagapps.medadh.dataClassReport.bodyRequest.Eritrosit
import com.gagapps.medadh.dataClassReport.bodyRequest.Hematokrit
import com.gagapps.medadh.dataClassReport.bodyRequest.Trombosit

data class Extension(
    val bcr_abl: BcrAbl,
    val hemoglobin: Hemoglobin,
    val note: String,
    val wblood_cell: WbloodCell,
    val trombosit: Trombosit,
    val hematokrit: Hematokrit,
    val eritrosit: Eritrosit
)