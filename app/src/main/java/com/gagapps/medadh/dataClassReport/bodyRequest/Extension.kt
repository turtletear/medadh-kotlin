package com.gagapps.medadh.dataClassReport.bodyRequest

data class Extension(
    val bcr_abl: BcrAbl,
    val hemoglobin: Hemoglobin,
    val note: String,
    val wblood_cell: WbloodCell,
    val trombosit: Trombosit,
    val hematokrit: Hematokrit,
    val eritrosit: Eritrosit
)