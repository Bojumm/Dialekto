package com.example.dialekto;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private Spinner dialect1;
    private Spinner dialect2;
    private ImageButton switchDialect;
    private EditText input;
    private TextView translation;
    // Create HashMaps for translation
    private HashMap<String, String> tagalogToKapampangan;
    private HashMap<String, String> kapampanganToTagalog;

    //For Icon
    private ImageView like, copy, speech;
    private TextToSpeech textToSpeech;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Home");

        View view = inflater.inflate(R.layout.home_fragment, null);
        dialect1 = view.findViewById(R.id.spinnerDialect1);
        dialect2 = view.findViewById(R.id.spinnerDialect2);
        switchDialect = view.findViewById(R.id.switchDialect);
        input = view.findViewById(R.id.etInput);
        translation = view.findViewById(R.id.tvTranslation);

        like = view.findViewById(R.id.likeIcon);
        copy = view.findViewById(R.id.copyIcon);
        speech = view.findViewById(R.id.volumeIcon);

        copy.setOnClickListener(v -> copyTextToClipboard());
        like.setOnClickListener(v -> AddToFavourite());

        // Initialize translation maps
        tagalogToKapampangan = new HashMap<>();
        kapampanganToTagalog = new HashMap<>();

        // Add translations to the maps
        tagalogToKapampangan.put("Ako", "Aku"); tagalogToKapampangan.put("ako", "aku"); // First person singular (I)
        tagalogToKapampangan.put("Ka", "Ka"); tagalogToKapampangan.put("ka", "ka"); // Second person singular (You - informal)
        tagalogToKapampangan.put("Siya", "Ya"); tagalogToKapampangan.put("siya", "ya"); // Third person singular (He/She/It)
        tagalogToKapampangan.put("Ikaw", "Ika"); tagalogToKapampangan.put("ikaw", "ika"); // Second person singular (You - more formal)
        tagalogToKapampangan.put("Siya (thing)", "Ita"); tagalogToKapampangan.put("siya (thing)", "ita"); // Third person singular (It - referring to things)
        tagalogToKapampangan.put("Kami", "Kami"); tagalogToKapampangan.put("kami", "kami"); // First person plural exclusive (We - not including the person spoken to)
        tagalogToKapampangan.put("Tayo", "Tamu"); tagalogToKapampangan.put("tayo", "tamu"); // First person plural inclusive (We - including the person spoken to)
        tagalogToKapampangan.put("Kayo", "Kayu"); tagalogToKapampangan.put("kayo", "kayu"); // Second person plural (You - plural)
        tagalogToKapampangan.put("Sila", "Ila"); tagalogToKapampangan.put("sila", "ila"); // Third person plural (They)
        tagalogToKapampangan.put("Ito", "Ini"); tagalogToKapampangan.put("ito", "ini"); // This (singular)
        tagalogToKapampangan.put("Nito", "Nini"); tagalogToKapampangan.put("nito", "nini"); // This (singular)
        tagalogToKapampangan.put("Sayo", "Keka"); tagalogToKapampangan.put("sayo", "keka"); // This (singular)
        tagalogToKapampangan.put("Inyo", "Kekayu"); tagalogToKapampangan.put("inyo", "kekayu"); // This (singular)


        // Markers/Linkers
        tagalogToKapampangan.put("Ang", "Ing"); tagalogToKapampangan.put("ang", "ing"); // Singular noun marker (The)
        tagalogToKapampangan.put("Ang mga", "Deng"); tagalogToKapampangan.put("ang mga", "deng"); // Plural noun marker (The - plural)
        tagalogToKapampangan.put("Ng", "Ning"); tagalogToKapampangan.put("ng", "ning"); // Genitive marker (of) - singular nouns
        tagalogToKapampangan.put("Ng mga", "Ding"); tagalogToKapampangan.put("ng mga", "ding"); // Genitive marker (of) - plural nouns
        tagalogToKapampangan.put("Nang", "Keng"); tagalogToKapampangan.put("nang", "keng"); // Genitive marker (of) - proper nouns or singular nouns starting with a vowel sound
        tagalogToKapampangan.put("Sa", "Keng"); tagalogToKapampangan.put("sa", "keng"); // Dative/Locative marker (to/for/at/in) - singular nouns or proper nouns
        tagalogToKapampangan.put("Sa mga", "Kareng"); tagalogToKapampangan.put("sa mga", "kareng"); // Dative/Locative marker (to/for/at/in) - plural nouns
        tagalogToKapampangan.put("At", "At"); tagalogToKapampangan.put("at", "at"); // Conjunction (and)
        tagalogToKapampangan.put("O", "O"); tagalogToKapampangan.put("o", "o"); // Conjunction (or)
        tagalogToKapampangan.put("Kung", "Nung"); tagalogToKapampangan.put("kung", "nung"); // Conjunction (if)
        tagalogToKapampangan.put("Dahil", "Uli"); tagalogToKapampangan.put("dahil", "uli"); // Conjunction (because of)
        tagalogToKapampangan.put("Para", "Para"); tagalogToKapampangan.put("para", "para"); // Preposition (for)
        tagalogToKapampangan.put("Gumawa", "Gewa"); tagalogToKapampangan.put("gumawa", "gewa"); // Do / Make
        tagalogToKapampangan.put("Lahat", "Ngan"); tagalogToKapampangan.put("lahat", "ngan"); // All

        // Common Verbs
        tagalogToKapampangan.put("Magkano", "Magkanu"); tagalogToKapampangan.put("magkano", "magkanu"); // How much / How many
        tagalogToKapampangan.put("Kumain", "Mangan"); tagalogToKapampangan.put("kumain", "mangan"); // Eat
        tagalogToKapampangan.put("Uminom", "Minum"); tagalogToKapampangan.put("uminom", "minum"); // Drink
        tagalogToKapampangan.put("Matulog", "Matudtud"); tagalogToKapampangan.put("matulog", "matudtud"); // Sleep
        tagalogToKapampangan.put("Magtrabaho", "Magobra"); tagalogToKapampangan.put("magtrabaho", "magobra"); // Work
        tagalogToKapampangan.put("Magsalita", "Magsalita"); tagalogToKapampangan.put("magsalita", "magsalita"); // Speak
        tagalogToKapampangan.put("Bumisita", "Mamisita"); tagalogToKapampangan.put("bumisita", "mamisita"); // Visit
        tagalogToKapampangan.put("Pumunta", "Munta"); tagalogToKapampangan.put("pumunta", "munta"); // Go
        tagalogToKapampangan.put("Umuwi", "Muli"); tagalogToKapampangan.put("umuwi", "muli"); // Go home
        tagalogToKapampangan.put("Makita", "Manakit"); tagalogToKapampangan.put("makita", "manakit"); // See
        tagalogToKapampangan.put("Alam", "Balu"); tagalogToKapampangan.put("alam", "balu"); // Know
        tagalogToKapampangan.put("Gusto", "Bisa"); tagalogToKapampangan.put("gusto", "bisa"); // Want
        tagalogToKapampangan.put("Gawin", "Gawa"); tagalogToKapampangan.put("gawin", "gawa"); // Do / Make
        tagalogToKapampangan.put("Kanina", "Napun"); tagalogToKapampangan.put("kanina", "napun"); // Earlier
        tagalogToKapampangan.put("Hinahanap", "Panintunan"); tagalogToKapampangan.put("hinahanap", "panintunan"); // Searching
        tagalogToKapampangan.put("Nakatira", "Makatuknangan"); tagalogToKapampangan.put("nakatira", "makatuknangan"); // Living

        // Common Adjectives
        tagalogToKapampangan.put("Maganda", "Mayap"); tagalogToKapampangan.put("maganda", "mayap"); // Good / Beautiful
        tagalogToKapampangan.put("Masama", "Marok"); tagalogToKapampangan.put("masama", "marok"); // Bad
        tagalogToKapampangan.put("Malaki", "Maragul"); tagalogToKapampangan.put("malaki", "maragul"); // Big
        tagalogToKapampangan.put("Maliit", "Malati"); tagalogToKapampangan.put("maliit", "malati"); // Small
        tagalogToKapampangan.put("Mabilis", "Mabilis"); tagalogToKapampangan.put("mabilis", "mabilis"); // Fast
        tagalogToKapampangan.put("Mabagal", "Mabagal"); tagalogToKapampangan.put("mabagal", "mabagal"); // Slow
        tagalogToKapampangan.put("Bago", "Bayu"); tagalogToKapampangan.put("bago", "bayu"); // New
        tagalogToKapampangan.put("Luma", "Dati"); tagalogToKapampangan.put("luma", "dati"); // Old

        // Common Nouns
        tagalogToKapampangan.put("Bahay", "Bale"); tagalogToKapampangan.put("bahay", "bale"); // House
        tagalogToKapampangan.put("Daan", "Dalan"); tagalogToKapampangan.put("daan", "dalan"); // Road
        tagalogToKapampangan.put("Tubig", "Danum"); tagalogToKapampangan.put("tubig", "danum"); // Water
        tagalogToKapampangan.put("Apoy", "Api"); tagalogToKapampangan.put("apoy", "api"); // Fire
        tagalogToKapampangan.put("Lupa", "Gabun"); tagalogToKapampangan.put("lupa", "gabun"); // Land / Ground
        tagalogToKapampangan.put("Hangin", "Angin"); tagalogToKapampangan.put("hangin", "angin"); // Wind
        tagalogToKapampangan.put("Araw", "Aldo"); tagalogToKapampangan.put("araw", "aldo"); // Day / Sun
        tagalogToKapampangan.put("Gabi", "Bengi"); tagalogToKapampangan.put("gabi", "bengi"); // Night
        tagalogToKapampangan.put("Anak", "Anak"); tagalogToKapampangan.put("anak", "anak"); // Child
        tagalogToKapampangan.put("Magulang", "Pengari"); tagalogToKapampangan.put("magulang", "pengari"); // Parents
        tagalogToKapampangan.put("Kapatid", "Kapatad"); tagalogToKapampangan.put("kapatid", "kapatad"); // Sibling
        tagalogToKapampangan.put("Aso", "Aclu"); tagalogToKapampangan.put("aso", "aclu"); // Dog
        tagalogToKapampangan.put("Pusa", "Pusa"); tagalogToKapampangan.put("pusa", "pusa"); // Cat
        tagalogToKapampangan.put("Manok", "Manuk"); tagalogToKapampangan.put("manok", "manuk"); // Chicken
        tagalogToKapampangan.put("Isda", "Isda"); tagalogToKapampangan.put("isda", "isda"); // Fish

        // Interrogatives
        tagalogToKapampangan.put("Ano", "Nanu"); tagalogToKapampangan.put("ano", "nanu"); // What
        tagalogToKapampangan.put("Sino", "Ninu"); tagalogToKapampangan.put("sino", "ninu"); // Who
        tagalogToKapampangan.put("Saan", "Nuqui"); tagalogToKapampangan.put("saan", "nuqui"); // Where
        tagalogToKapampangan.put("Paano", "Makananu"); tagalogToKapampangan.put("paano", "makananu"); // How
        tagalogToKapampangan.put("Kailan", "Kapilan"); tagalogToKapampangan.put("kailan", "kapilan"); // When
        tagalogToKapampangan.put("Bakit", "Bakit"); tagalogToKapampangan.put("bakit", "bakit"); // Why

        // Common Phrases
        tagalogToKapampangan.put("Magandang", "Mayap"); tagalogToKapampangan.put("magandang", "mayap");
        tagalogToKapampangan.put("Umaga", "Abak"); tagalogToKapampangan.put("umaga", "abak"); // Morning
        tagalogToKapampangan.put("Hapon", "Gatpanapun"); tagalogToKapampangan.put("hapon", "gatpanapun"); // Afternoon
        tagalogToKapampangan.put("Kahapon", "Napun"); tagalogToKapampangan.put("kahapon", "napun"); // Yesterday
        tagalogToKapampangan.put("Gabi", "Bengi"); tagalogToKapampangan.put("gabi", "bengi"); // Evening / Night
        tagalogToKapampangan.put("Kagabi", "Nabengi"); tagalogToKapampangan.put("kagabi", "nabengi"); // Last night
        tagalogToKapampangan.put("Kamusta", "Komusta"); tagalogToKapampangan.put("kamusta", "komusta"); // How are you?
        tagalogToKapampangan.put("Mabuti", "Mayap"); tagalogToKapampangan.put("mabuti", "mayap"); // I am fine
        tagalogToKapampangan.put("Salamat", "Salamat"); tagalogToKapampangan.put("salamat", "salamat"); // Thank you
        tagalogToKapampangan.put("Oo", "Wa"); tagalogToKapampangan.put("oo", "wa"); // Yes
        tagalogToKapampangan.put("Hindi", "Ali"); tagalogToKapampangan.put("hindi", "ali"); // No

        // More Common Verbs
        tagalogToKapampangan.put("Mag-aral", "Mag aral"); tagalogToKapampangan.put("mag aral", "magaral"); // Study
        tagalogToKapampangan.put("Magbasa", "Mambasa"); tagalogToKapampangan.put("magbasa", "mambasa"); // Read
        tagalogToKapampangan.put("Makinig", "Makiramdam"); tagalogToKapampangan.put("makinig", "makiramdam"); // Listen
        tagalogToKapampangan.put("Manood", "Manalbe"); tagalogToKapampangan.put("manood", "manalbe"); // Watch
        tagalogToKapampangan.put("Magmahal", "Malugud"); tagalogToKapampangan.put("magmahal", "malugud"); // Love
        tagalogToKapampangan.put("Magalit", "Mimwa"); tagalogToKapampangan.put("magalit", "mimwa"); // Be angry
        tagalogToKapampangan.put("Sumali", "Sasali"); tagalogToKapampangan.put("sumali", "sasali"); // Join
        tagalogToKapampangan.put("Tumutok", "Tutuk"); tagalogToKapampangan.put("tumutok", "tutuk"); // Focus
        tagalogToKapampangan.put("Sumunod", "Susuyu"); tagalogToKapampangan.put("sumunod", "susuyu"); // Obey
        tagalogToKapampangan.put("Magbigay", "Bibye"); tagalogToKapampangan.put("magbigay", "bibye"); // Give
        tagalogToKapampangan.put("Tanggapin", "Tanggap"); tagalogToKapampangan.put("tanggapin", "tanggap"); // Receive
        tagalogToKapampangan.put("Sumigaw", "Ginulisak"); tagalogToKapampangan.put("sumigaw", "ginulisak"); // Shout
        tagalogToKapampangan.put("Tumawa", "Tatawa"); tagalogToKapampangan.put("tumawa", "tatawa"); // Laugh
        // More Verbs
        tagalogToKapampangan.put("Mangyari", "Maniari"); tagalogToKapampangan.put("mangyari", "maniari");  // Happen

        // More Adjectives
        tagalogToKapampangan.put("Makulit", "Makulit"); tagalogToKapampangan.put("makulit", "makulit");  // Annoying / Persistent
        tagalogToKapampangan.put("Masaya", "Masaya"); tagalogToKapampangan.put("masaya", "masaya");  // Happy
        tagalogToKapampangan.put("Malungkot", "Malungkut"); tagalogToKapampangan.put("malungkot", "malungkut");  // Sad
        tagalogToKapampangan.put("Tamad", "Matamad"); tagalogToKapampangan.put("tamad", "matamad");  // Lazy
        tagalogToKapampangan.put("Masipag", "Masipag"); tagalogToKapampangan.put("masipag", "masipag");  // Hardworking
        tagalogToKapampangan.put("Matalino", "Matalino"); tagalogToKapampangan.put("matalino", "matalino");  // Smart
        tagalogToKapampangan.put("Bobo", "Bobo"); tagalogToKapampangan.put("bobo", "bobo");  // Stupid
        tagalogToKapampangan.put("Makasalanan", "Makasalanan"); tagalogToKapampangan.put("makasalanan", "makasalanan");  // Sinful
        tagalogToKapampangan.put("Malinis", "Malinis"); tagalogToKapampangan.put("malinis", "malinis");  // Clean
        tagalogToKapampangan.put("Marumi", "Marumi"); tagalogToKapampangan.put("marumi", "marumi");  // Dirty
        tagalogToKapampangan.put("Mabaho", "Makabsi"); tagalogToKapampangan.put("mabaho", "makabsi");  // Smelly
        tagalogToKapampangan.put("Mabango", "Mabango"); tagalogToKapampangan.put("mabango", "mabango");  // Fragrant
        tagalogToKapampangan.put("Pagod", "Mapagal"); tagalogToKapampangan.put("pagod", "mapagal");  // Tired
        tagalogToKapampangan.put("Malakas", "Masikan"); tagalogToKapampangan.put("malakas", "masikan");  // Strong / Loud
        tagalogToKapampangan.put("Mahina", "Maluya"); tagalogToKapampangan.put("mahina", "maluya");  // Weak

        // More Adverbs
        tagalogToKapampangan.put("Agad", "Agad"); tagalogToKapampangan.put("agad", "agad");  // Immediately
        tagalogToKapampangan.put("Noon", "Lawas"); tagalogToKapampangan.put("noon", "lawas");  // Ago / Past
        tagalogToKapampangan.put("Mamayang", "Bukas a"); tagalogToKapampangan.put("mamayang", "bukas a");  // Later this morning
        tagalogToKapampangan.put("Wala", "Alang"); tagalogToKapampangan.put("wala", "alang");  // Not / None
        tagalogToKapampangan.put("Siguro", "Siguru"); tagalogToKapampangan.put("siguro", "siguru");  // Maybe / Perhaps
        tagalogToKapampangan.put("Tabi", "Tabi"); tagalogToKapampangan.put("tabi", "tabi");  // Aside
        tagalogToKapampangan.put("Ilalim", "Lalam"); tagalogToKapampangan.put("ilalim", "lalam");  // Under
        tagalogToKapampangan.put("Ibabaw", "Babo"); tagalogToKapampangan.put("ibabaw", "babo");  // Above / On top

        // More Nouns
        tagalogToKapampangan.put("Oras", "Oras"); tagalogToKapampangan.put("oras", "oras");  // Hour / Time
        tagalogToKapampangan.put("Minuto", "Minuto"); tagalogToKapampangan.put("minuto", "minuto");  // Minute
        tagalogToKapampangan.put("Segundo", "Segundo"); tagalogToKapampangan.put("segundo", "segundo");  // Second
        tagalogToKapampangan.put("Buwan", "Bulan"); tagalogToKapampangan.put("buwan", "bulan");  // Month / Moon
        tagalogToKapampangan.put("Taon", "Banua"); tagalogToKapampangan.put("taon", "banua");  // Year / Sky
        tagalogToKapampangan.put("Taong", "Banua"); tagalogToKapampangan.put("taong", "banua");  // Year / Sky
        tagalogToKapampangan.put("Pamilya", "Familia"); tagalogToKapampangan.put("pamilya", "familia");  // Family
        tagalogToKapampangan.put("Kaibigan", "Kaibigan"); tagalogToKapampangan.put("kaibigan", "kaibigan");  // Friend
        tagalogToKapampangan.put("Tinda", "Tinda"); tagalogToKapampangan.put("tinda", "tinda");  // Merchandise / Goods for sale
        tagalogToKapampangan.put("Simbahan", "Pisamban"); tagalogToKapampangan.put("simbahan", "pisamban");  // Church
        tagalogToKapampangan.put("Paaralan", "Iskwela"); tagalogToKapampangan.put("paaralan", "iskwela");  // School
        tagalogToKapampangan.put("Ospital", "Ospital"); tagalogToKapampangan.put("ospital", "ospital");  // Hospital
        tagalogToKapampangan.put("Kahoy", "Dutung"); tagalogToKapampangan.put("kahoy", "dutung");  // Wood
        tagalogToKapampangan.put("Bulaklak", "Bulaklak"); tagalogToKapampangan.put("bulaklak", "bulaklak");  // Flower
        tagalogToKapampangan.put("Bunga", "Bunga / Prutas"); tagalogToKapampangan.put("bunga", "bunga / prutas");  // Fruit
        tagalogToKapampangan.put("Gulayan", "Ubingan"); tagalogToKapampangan.put("gulayan", "ubingan");  // Vegetable garden
        tagalogToKapampangan.put("Pangalan", "Lagyo"); tagalogToKapampangan.put("pangalan", "lagyo");  // Name

        // More Interrogatives
        tagalogToKapampangan.put("Ilan", "Pilan"); tagalogToKapampangan.put("ilan", "pilan");  // How many / How much

        // Vise versa
        kapampanganToTagalog.put("Aku", "Ako"); kapampanganToTagalog.put("aku", "ako");  // First person singular (I)
        kapampanganToTagalog.put("Ka", "Ka"); kapampanganToTagalog.put("ka", "ka");  // Second person singular (You - informal)
        kapampanganToTagalog.put("Ya", "Siya"); kapampanganToTagalog.put("ya", "siya");  // Third person singular (He/She/It)
        kapampanganToTagalog.put("Ika", "Ikaw"); kapampanganToTagalog.put("ika", "ikaw");  // Second person singular (You - more formal)
        kapampanganToTagalog.put("Ita", "Siya (thing)"); kapampanganToTagalog.put("ita", "siya (thing)");  // Third person singular (It - referring to things)
        kapampanganToTagalog.put("Kami", "Kami"); kapampanganToTagalog.put("kami", "kami");  // First person plural exclusive (We - not including the person spoken to)
        kapampanganToTagalog.put("Tamu", "Tayo"); kapampanganToTagalog.put("tamu", "tayo");  // First person plural inclusive (We - including the person spoken to)
        kapampanganToTagalog.put("Kayu", "Kayo"); kapampanganToTagalog.put("kayu", "kayo");  // Second person plural (You - plural)
        kapampanganToTagalog.put("Ila", "Sila"); kapampanganToTagalog.put("ila", "sila");  // Third person plural (They)
        kapampanganToTagalog.put("Ini", "Ito"); kapampanganToTagalog.put("ini", "ito");  // Third person plural
        kapampanganToTagalog.put("Nini", "Nito"); kapampanganToTagalog.put("nini", "nito");  // Good afternoon
        kapampanganToTagalog.put("Keka", "Sayo"); kapampanganToTagalog.put("keka", "sayo"); // This (singular)
        kapampanganToTagalog.put("Kekayu", "Inyo"); kapampanganToTagalog.put("kekayu", "inyo"); // This (singular)

        // Markers/Linkers
        kapampanganToTagalog.put("Ing", "Ang"); kapampanganToTagalog.put("ing", "ang");  // Singular noun marker (The)
        kapampanganToTagalog.put("Deng", "Ang mga"); kapampanganToTagalog.put("deng", "ang mga");  // Plural noun marker (The - plural)
        kapampanganToTagalog.put("Ning", "Ng"); kapampanganToTagalog.put("ning", "ng");  // Genitive marker (of) - singular nouns
        kapampanganToTagalog.put("Ding", "Ng mga"); kapampanganToTagalog.put("ding", "ng mga");  // Genitive marker (of) - plural nouns
        kapampanganToTagalog.put("Keng", "Nang"); kapampanganToTagalog.put("keng", "nang");  // Genitive marker (of) - proper nouns or singular nouns starting with a vowel sound
        kapampanganToTagalog.put("Keng", "Sa"); kapampanganToTagalog.put("keng", "sa");  // Dative/Locative marker (to/for/at/in) - singular nouns or proper nouns
        kapampanganToTagalog.put("Kareng", "Sa mga"); kapampanganToTagalog.put("kareng", "sa mga");  // Dative/Locative marker (to/for/at/in) - plural nouns
        kapampanganToTagalog.put("At", "At"); kapampanganToTagalog.put("at", "at");  // Conjunction (and)
        kapampanganToTagalog.put("O", "O"); kapampanganToTagalog.put("o", "o");  // Conjunction (or)
        kapampanganToTagalog.put("Nung", "Kung"); kapampanganToTagalog.put("nung", "kung");  // Conjunction (if)
        kapampanganToTagalog.put("Uli", "Dahil"); kapampanganToTagalog.put("uli", "dahil");  // Conjunction (because of)
        kapampanganToTagalog.put("Para", "Para"); kapampanganToTagalog.put("para", "para");  // Preposition (for)
        kapampanganToTagalog.put("Gewa", "Gumawa"); kapampanganToTagalog.put("gewa", "gumawa");  // Good afternoon
        kapampanganToTagalog.put("Ngan", "Lahat"); kapampanganToTagalog.put("ngan", "lahat");  // Good afternoon

        // Common Verbs
        kapampanganToTagalog.put("Magkanu", "Magkano"); kapampanganToTagalog.put("magkanu", "magkano");  // Eat
        kapampanganToTagalog.put("Mangan", "Kumain"); kapampanganToTagalog.put("mangan", "kumain");  // Eat
        kapampanganToTagalog.put("Minum", "Uminom"); kapampanganToTagalog.put("minum", "uminom");  // Drink
        kapampanganToTagalog.put("Matudtud", "Matulog"); kapampanganToTagalog.put("matudtud", "matulog");  // Sleep
        kapampanganToTagalog.put("Magobra", "Magtrabaho"); kapampanganToTagalog.put("magobra", "magtrabaho");  // Work
        kapampanganToTagalog.put("Magsalita", "Magsalita"); kapampanganToTagalog.put("magsalita", "magsalita");  // Speak
        kapampanganToTagalog.put("Mamisita", "Bumisita"); kapampanganToTagalog.put("mamisita", "bumisita");  // Visit
        kapampanganToTagalog.put("Munta", "Pumunta"); kapampanganToTagalog.put("munta", "pumunta");  // Go
        kapampanganToTagalog.put("Muli", "Umuwi"); kapampanganToTagalog.put("muli", "umuwi");  // Go home
        kapampanganToTagalog.put("Manakit", "Makita"); kapampanganToTagalog.put("manakit", "makita");  // See
        kapampanganToTagalog.put("Balu", "Alam"); kapampanganToTagalog.put("balu", "alam");  // Know
        kapampanganToTagalog.put("Bisa", "Gusto"); kapampanganToTagalog.put("bisa", "gusto");  // Want
        kapampanganToTagalog.put("Gawa", "Gawin"); kapampanganToTagalog.put("gawa", "gawin");  // Do / Make
        kapampanganToTagalog.put("Napun", "Kanina"); kapampanganToTagalog.put("napun", "kanina");  // Eat
        kapampanganToTagalog.put("Panintunan", "Hinahanap"); kapampanganToTagalog.put("panintunan", "hinahanap");  // Eat
        kapampanganToTagalog.put("Makatuknangan", "Nakatira"); kapampanganToTagalog.put("makatuknangan", "nakatira");  // Good afternoon

        // Common Adjectives
        kapampanganToTagalog.put("Mayap", "Maganda"); kapampanganToTagalog.put("mayap", "maganda");  // Good / Beautiful
        kapampanganToTagalog.put("Marok", "Masama"); kapampanganToTagalog.put("marok", "masama");  // Bad
        kapampanganToTagalog.put("Maragul", "Malaki"); kapampanganToTagalog.put("maragul", "malaki");  // Big
        kapampanganToTagalog.put("Malati", "Maliit"); kapampanganToTagalog.put("malati", "maliit");  // Small
        kapampanganToTagalog.put("Mabilis", "Mabilis"); kapampanganToTagalog.put("mabilis", "mabilis");  // Fast
        kapampanganToTagalog.put("Mabagal", "Mabagal"); kapampanganToTagalog.put("mabagal", "mabagal");  // Slow
        kapampanganToTagalog.put("Bayu", "Bago"); kapampanganToTagalog.put("bayu", "bago");  // New
        kapampanganToTagalog.put("Dati", "Luma"); kapampanganToTagalog.put("dati", "luma");  // Old

        // Common Nouns
        kapampanganToTagalog.put("Bale", "Bahay"); kapampanganToTagalog.put("bale", "bahay");  // House
        kapampanganToTagalog.put("Dalan", "Daan"); kapampanganToTagalog.put("dalan", "daan");  // Road
        kapampanganToTagalog.put("Danum", "Tubig"); kapampanganToTagalog.put("danum", "tubig");  // Water
        kapampanganToTagalog.put("Api", "Apoy"); kapampanganToTagalog.put("api", "apoy");  // Fire
        kapampanganToTagalog.put("Gabun", "Lupa"); kapampanganToTagalog.put("gabun", "lupa");  // Land / Ground
        kapampanganToTagalog.put("Angin", "Hangin"); kapampanganToTagalog.put("angin", "hangin");  // Wind
        kapampanganToTagalog.put("Aldo", "Araw"); kapampanganToTagalog.put("aldo", "araw");  // Day / Sun
        kapampanganToTagalog.put("Bengi", "Gabi"); kapampanganToTagalog.put("bengi", "gabi");  // Night
        kapampanganToTagalog.put("Anak", "Anak"); kapampanganToTagalog.put("anak", "anak");  // Child
        kapampanganToTagalog.put("Pengari", "Magulang"); kapampanganToTagalog.put("pengari", "magulang");  // Parents
        kapampanganToTagalog.put("Kapatad", "Kapatid"); kapampanganToTagalog.put("kapatad", "kapatid");  // Sibling
        kapampanganToTagalog.put("Aclu", "Aso"); kapampanganToTagalog.put("aclu", "aso");  // Dog
        kapampanganToTagalog.put("Pusa", "Pusa"); kapampanganToTagalog.put("pusa", "pusa");  // Cat
        kapampanganToTagalog.put("Manuk", "Manok"); kapampanganToTagalog.put("manuk", "manok");  // Chicken
        kapampanganToTagalog.put("Isda", "Isda"); kapampanganToTagalog.put("isda", "isda");  // Fish

        // Interrogatives
        kapampanganToTagalog.put("Nanu", "Ano"); kapampanganToTagalog.put("nanu", "ano");  // What
        kapampanganToTagalog.put("Ninu", "Sino"); kapampanganToTagalog.put("ninu", "sino");  // Who
        kapampanganToTagalog.put("Nuqui", "Saan"); kapampanganToTagalog.put("nuqui", "saan");  // Where
        kapampanganToTagalog.put("Makananu", "Paano"); kapampanganToTagalog.put("makananu", "paano");  // How
        kapampanganToTagalog.put("Kapilan", "Kailan"); kapampanganToTagalog.put("kapilan", "kailan");  // When
        kapampanganToTagalog.put("Bakit", "Bakit"); kapampanganToTagalog.put("bakit", "bakit");  // Why

        // Common Phrases
        kapampanganToTagalog.put("Mayap", "Magandang"); kapampanganToTagalog.put("mayap", "magandang");
        kapampanganToTagalog.put("Abak", "Umaga"); kapampanganToTagalog.put("abak", "umaga"); // Morning
        kapampanganToTagalog.put("Gatpanapun", "Hapon"); kapampanganToTagalog.put("gatpanapun", "hapon"); // Afternoon
        kapampanganToTagalog.put("Napun", "Kahapon"); kapampanganToTagalog.put("napun", "kahapon"); // Yesterday
        kapampanganToTagalog.put("Bengi", "Gabi"); kapampanganToTagalog.put("bengi", "gabi"); // Evening / Night
        kapampanganToTagalog.put("Nabengi", "Kagabi"); kapampanganToTagalog.put("nabengi", "kagabi"); // Last night
        kapampanganToTagalog.put("Komusta", "Kamusta"); kapampanganToTagalog.put("komusta", "kamusta"); // How are you?
        kapampanganToTagalog.put("Salamat", "Salamat"); kapampanganToTagalog.put("salamat", "salamat"); // Thank you
        kapampanganToTagalog.put("Wa", "Oo"); kapampanganToTagalog.put("wa", "oo"); // Yes
        kapampanganToTagalog.put("Ali", "Hindi"); kapampanganToTagalog.put("ali", "hindi"); // No

        // More Common Verbs
        kapampanganToTagalog.put("Magaral", "Mag aral"); kapampanganToTagalog.put("magaral", "mag aral"); // Study
        kapampanganToTagalog.put("Mambasa", "Magbasa"); kapampanganToTagalog.put("mambasa", "magbasa"); // Read
        kapampanganToTagalog.put("Makiramdam", "Makinig"); kapampanganToTagalog.put("makiramdam", "makinig"); // Listen
        kapampanganToTagalog.put("Manalbe", "Manood"); kapampanganToTagalog.put("manalbe", "manood"); // Watch
        kapampanganToTagalog.put("Malugud", "Magmahal"); kapampanganToTagalog.put("malugud", "magmahal"); // Love
        kapampanganToTagalog.put("Mimwa", "Magalit"); kapampanganToTagalog.put("mimwa", "magalit"); // Be angry
        kapampanganToTagalog.put("Sasali", "Sumali"); kapampanganToTagalog.put("sasali", "sumali"); // Join
        kapampanganToTagalog.put("Tutuk", "Tumutok"); kapampanganToTagalog.put("tutuk", "tumutok"); // Focus
        kapampanganToTagalog.put("Susuyu", "Sumunod"); kapampanganToTagalog.put("susuyu", "sumunod"); // Obey
        kapampanganToTagalog.put("Bibye", "Magbigay"); kapampanganToTagalog.put("bibye", "magbigay"); // Give
        kapampanganToTagalog.put("Tanggap", "Tanggapin"); kapampanganToTagalog.put("tanggap", "tanggapin"); // Receive
        kapampanganToTagalog.put("Ginulisak", "Sumigaw"); kapampanganToTagalog.put("ginulisak", "sumigaw"); // Shout
        kapampanganToTagalog.put("Tatawa", "Tumawa"); kapampanganToTagalog.put("tatawa", "tumawa"); // Laugh
        // More Verbs
        kapampanganToTagalog.put("Maniari", "Mangyari"); kapampanganToTagalog.put("maniari", "mangyari");  // Happen

        // More Adjectives
        kapampanganToTagalog.put("Makulit", "Makulit"); kapampanganToTagalog.put("makulit", "makulit");  // Annoying / Persistent
        kapampanganToTagalog.put("Masaya", "Masaya"); kapampanganToTagalog.put("masaya", "masaya");  // Happy
        kapampanganToTagalog.put("Malungkut", "Malungkot"); kapampanganToTagalog.put("malungkut", "malungkot");  // Sad
        kapampanganToTagalog.put("Matamad", "Tamad"); kapampanganToTagalog.put("matamad", "tamad");  // Lazy
        kapampanganToTagalog.put("Masipag", "Masipag"); kapampanganToTagalog.put("masipag", "masipag");  // Hardworking
        kapampanganToTagalog.put("Matalino", "Matalino"); kapampanganToTagalog.put("matalino", "matalino");  // Smart
        kapampanganToTagalog.put("Bobo", "Bobo"); kapampanganToTagalog.put("bobo", "bobo");  // Stupid
        kapampanganToTagalog.put("Makasalanan", "Makasalanan"); kapampanganToTagalog.put("makasalanan", "makasalanan");  // Sinful
        kapampanganToTagalog.put("Malinis", "Malinis"); kapampanganToTagalog.put("malinis", "malinis");  // Clean
        kapampanganToTagalog.put("Marumi", "Marumi"); kapampanganToTagalog.put("marumi", "marumi");  // Dirty
        kapampanganToTagalog.put("Makabsi", "Mabaho"); kapampanganToTagalog.put("makabsi", "mabaho");  // Smelly
        kapampanganToTagalog.put("Mabango", "Mabango"); kapampanganToTagalog.put("mabango", "mabango");  // Fragrant
        kapampanganToTagalog.put("Mapagal", "Pagod"); kapampanganToTagalog.put("mapagal", "pagod");  // Tired
        kapampanganToTagalog.put("Masikan", "Malakas"); kapampanganToTagalog.put("masikan", "malakas");  // Strong / Loud
        kapampanganToTagalog.put("Maluya", "Mahina"); kapampanganToTagalog.put("maluya", "mahina");  // Weak

        // More Adverbs
        kapampanganToTagalog.put("Agad", "Agad"); kapampanganToTagalog.put("agad", "agad");  // Immediately
        kapampanganToTagalog.put("Lawas", "Noon"); kapampanganToTagalog.put("lawas", "noon");  // Ago / Past
        kapampanganToTagalog.put("Bukas a", "Mamayang"); kapampanganToTagalog.put("bukas a", "mamayang");  // Later this morning
        kapampanganToTagalog.put("Alang", "Wala"); kapampanganToTagalog.put("alang", "wala");  // Not / None
        kapampanganToTagalog.put("Siguru", "Siguro"); kapampanganToTagalog.put("siguru", "siguro");  // Maybe / Perhaps
        kapampanganToTagalog.put("Tabi", "Tabi"); kapampanganToTagalog.put("tabi", "tabi");  // Aside
        kapampanganToTagalog.put("Lalam", "Ilalim"); kapampanganToTagalog.put("lalam", "ilalim");  // Under
        kapampanganToTagalog.put("Babo", "Ibabaw"); kapampanganToTagalog.put("babo", "ibabaw");  // Above / On top

        // More Nouns
        kapampanganToTagalog.put("Oras", "Oras"); kapampanganToTagalog.put("oras", "oras");  // Hour / Time
        kapampanganToTagalog.put("Minuto", "Minuto"); kapampanganToTagalog.put("minuto", "minuto");  // Minute
        kapampanganToTagalog.put("Segundo", "Segundo"); kapampanganToTagalog.put("segundo", "segundo");  // Second
        kapampanganToTagalog.put("Bulan", "Buwan"); kapampanganToTagalog.put("bulan", "buwan");  // Month / Moon
        kapampanganToTagalog.put("Banua", "Taon"); kapampanganToTagalog.put("banua", "taon");  // Year / Sky
        kapampanganToTagalog.put("Banua", "Taong"); kapampanganToTagalog.put("banua", "taong");  // Year / Sky
        kapampanganToTagalog.put("Familia", "Pamilya"); kapampanganToTagalog.put("familia", "pamilya");  // Family
        kapampanganToTagalog.put("Kaibigan", "Kaibigan"); kapampanganToTagalog.put("kaibigan", "kaibigan");  // Friend
        kapampanganToTagalog.put("Tinda", "Tinda"); kapampanganToTagalog.put("tinda", "tinda");  // Merchandise / Goods for sale
        kapampanganToTagalog.put("Pisamban", "Simbahan"); kapampanganToTagalog.put("pisamban", "simbahan");  // Church
        kapampanganToTagalog.put("Iskwela", "Paaralan"); kapampanganToTagalog.put("iskwela", "paaralan");  // School
        kapampanganToTagalog.put("Ospital", "Ospital"); kapampanganToTagalog.put("ospital", "ospital");  // Hospital
        kapampanganToTagalog.put("Dutung", "Kahoy"); kapampanganToTagalog.put("dutung", "kahoy");  // Wood
        kapampanganToTagalog.put("Bulaklak", "Bulaklak"); kapampanganToTagalog.put("bulaklak", "bulaklak");  // Flower
        kapampanganToTagalog.put("Bunga / Prutas", "Bunga"); kapampanganToTagalog.put("bunga / prutas", "bunga");  // Fruit
        kapampanganToTagalog.put("Ubingan", "Gulayan"); kapampanganToTagalog.put("ubingan", "gulayan");  // Vegetable garden
        kapampanganToTagalog.put("Lagyo", "Pangalan"); kapampanganToTagalog.put("lagyo", "pangalan");  // Name

        // More Interrogatives
        kapampanganToTagalog.put("Pilan", "Ilan"); kapampanganToTagalog.put("pilan", "ilan");  // How many / How much

        List<String> dialects = new ArrayList<>();
        dialects.add("Tagalog");
        dialects.add("Kapampangan");

        // Create separate adapters for dialect1 and dialect2
        ArrayAdapter<String> dialect1Adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, dialects);
        dialect1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> dialect2Adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, dialects);
        dialect2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Apply the adapters to the Spinners
        dialect1.setAdapter(dialect1Adapter);
        dialect2.setAdapter(dialect2Adapter);

        // Manually set the selection for each Spinner
        dialect1.setSelection(0);  // This will set "Tagalog" in dialect1
        dialect2.setSelection(1);  // This will set "Kapampangan" in dialect2

        // Set up the TextWatcher to update translation as the user types
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Get the text input by the user
                String inputText = charSequence.toString().trim();

                // Get the current selected items from both Spinners
                String dialect1Value = dialect1.getSelectedItem().toString();
                String dialect2Value = dialect2.getSelectedItem().toString();

                // Split the input text into words
                String[] words = inputText.split(" ");

                // Create a StringBuilder to build the translated sentence
                StringBuilder translatedText = new StringBuilder();

                // Check the current selection and provide the correct translation
                if (dialect1Value.equals("Tagalog") && dialect2Value.equals("Kapampangan")) {
                    // Translate each word from Tagalog to Kapampangan
                    for (String word : words) {
                        // Check if the word exists in the translation map
                        String translatedWord = tagalogToKapampangan.get(word);
                        if (translatedWord != null) {
                            translatedText.append(translatedWord);  // Add translated word
                        } else {
                            translatedText.append(word);  // Keep the word as-is if no translation found
                        }
                        translatedText.append(" ");  // Add a space between words
                    }
                } else if (dialect1Value.equals("Kapampangan") && dialect2Value.equals("Tagalog")) {
                    // Translate each word from Kapampangan to Tagalog
                    for (String word : words) {
                        // Check if the word exists in the translation map
                        String translatedWord = kapampanganToTagalog.get(word);
                        if (translatedWord != null) {
                            translatedText.append(translatedWord);  // Add translated word
                        } else {
                            translatedText.append(word);  // Keep the word as-is if no translation found
                        }
                        translatedText.append(" ");  // Add a space between words
                    }
                }

                // Trim any extra space at the end
                String finalTranslation = translatedText.toString().trim();

                // If a translation exists, display it
                if (!finalTranslation.isEmpty()) {
                    translation.setText(finalTranslation);  // Display the translation in the TextView
                } else {
                    translation.setText("Translation not available.");  // If no translation found
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set the click listener for the switchDialect button
        switchDialect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the current selected items from both Spinners
                String dialect1Value = dialect1.getSelectedItem().toString();
                String dialect2Value = dialect2.getSelectedItem().toString();

                // Swap selections in the spinners
                dialect1.setSelection(((ArrayAdapter<String>) dialect1.getAdapter()).getPosition(dialect2Value));
                dialect2.setSelection(((ArrayAdapter<String>) dialect2.getAdapter()).getPosition(dialect1Value));

                // Swap the input text and the translated text based on the current dialect selections
                String temp = input.getText().toString().trim();  // Get the current text from the input field
                String tempTranslation = translation.getText().toString().trim();

                // Check if the translation is "Translation Not Available"
                if (tempTranslation.equals("Translation not available.")) {
                    // If translation is not available, clear both fields
                    input.setText("");
                    translation.setText("Translation");
                } else {
                        // Swap the values
                        input.setText(tempTranslation);  // Set the input field to show the translation
                        translation.setText(temp);}
            }
        });

        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Set the language to Tagalog
                Locale tagalogLocale = new Locale("tl", "PH");
                int result = textToSpeech.setLanguage(tagalogLocale);
            } else {
                Toast.makeText(getContext(), "Text-to-Speech initialization failed.", Toast.LENGTH_LONG).show();
            }
        });

        speech.setOnClickListener(v -> {
            String textToSpeak = translation.getText().toString();
            if (!textToSpeak.equals("Translation") && !textToSpeak.equals("Translation not available.")) {
                if (textToSpeech != null) {
                    textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
        speech.setOnClickListener(v -> {
            String textToSpeak = translation.getText().toString();
            if (textToSpeak.equals("") || textToSpeak.equals("Translation..") || textToSpeak.equals("Translation not available.")) {
                Toast.makeText(getContext(), "No translation Available.", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("No Translation Available", TextToSpeech.QUEUE_FLUSH, null, null);
            }
            else{
                if (textToSpeech != null) {
                    textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
        return view;
    }
    private void AddToFavourite(){
        String inputText = input.getText().toString().trim();
        String translationText = translation.getText().toString().trim();

        // Get selected languages
        String inputLanguage = dialect1.getSelectedItem().toString();
        String translationLanguage = dialect2.getSelectedItem().toString();

        if (!TextUtils.isEmpty(inputText) && !TextUtils.isEmpty(translationText) &&
                !translationText.equals("Translation") && !translationText.equals("Translation not available.")) {

            // Add to favorites with language info
            CardItem favoriteItem = new CardItem(inputText, translationText, inputLanguage, translationLanguage);

            // Pass the context to getInstance()
            FavoritesManager.getInstance(requireContext()).addFavorite(favoriteItem);

            // Notify the user
            Toast.makeText(getContext(), "Added to favorites!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No valid input or translation to add to favorites.", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyTextToClipboard() {
        String textToCopy = translation.getText().toString();

        // Check if the TextView has any text
        if (!TextUtils.isEmpty(textToCopy)) {
            // Get the ClipboardManager system service
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

            // Create a ClipData with the text to copy
            android.content.ClipData clip = android.content.ClipData.newPlainText("Recognized Text", textToCopy);

            // Set the clip data to the clipboard
            clipboard.setPrimaryClip(clip);

            // Show a Toast to notify the user
            Toast.makeText(getContext(), "Text copied to clipboard!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No text to copy", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
