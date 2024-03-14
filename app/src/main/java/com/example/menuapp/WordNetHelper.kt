package com.example.sql

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import edu.mit.jwi.Dictionary
import edu.mit.jwi.item.POS
import edu.mit.jwi.item.Pointer
import edu.mit.jwi.morph.WordnetStemmer

class WordNetHelper {

    fun getWrd(context: Context): Dictionary {

        val dir = File(context.filesDir, "dict_files")
        dir.mkdir()

        val dir_list = context.assets.list("dict")

        if (dir_list != null) {
            for (ind in dir_list) {

                val file_aset = context.assets.open("dict/${ind}")

                val file_obj = File(dir, ind)

                file_aset?.use { input ->
                    FileOutputStream(file_obj).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }

        val dict = Dictionary(dir)
        dict.open()

        return dict
    }

    fun getText(JWI_DICT : Dictionary, text_inp : String): ArrayList<String>? {

        if(text_inp.isBlank() == true)
            return null

        val pointer_list_syn = ArrayList<Pointer>()

        pointer_list_syn.add(Pointer.SIMILAR_TO)
        pointer_list_syn.add(Pointer.HYPERNYM)
        pointer_list_syn.add(Pointer.HOLONYM_MEMBER)
        pointer_list_syn.add(Pointer.HYPONYM)
        pointer_list_syn.add(Pointer.HOLONYM_SUBSTANCE)
        pointer_list_syn.add(Pointer.HOLONYM_PART)
        pointer_list_syn.add(Pointer.ATTRIBUTE)
        pointer_list_syn.add(Pointer.DERIVATIONALLY_RELATED)
        pointer_list_syn.add(Pointer.MERONYM_MEMBER)
        pointer_list_syn.add(Pointer.MERONYM_PART)

        val str_array_final = ArrayList<String>()                                 //temporary container

        for (ind in pointer_list_syn) {

            for (POS in POS.values()) {

                val word_index = JWI_DICT.getIndexWord(text_inp, POS)
                val str_array = ArrayList<String>()

                word_index?.let { wd ->

                    val wdId = wd.wordIDs.get(0)
                    val wd_synset = JWI_DICT.getSynset(wdId.synsetID)

                    val arr_array = wd_synset.getRelatedSynsets(ind)

                    for (x in arr_array) {
                        str_array.add(JWI_DICT.getSynset(x).words[0].lemma)

                        if (str_array.size == 8)
                            break
                    }
                }

                str_array_final.addAll(str_array)

                if (str_array.size == 25)
                    break
            }
        }

        if(str_array_final.size > 0)
            return str_array_final

        return null
    }
}
