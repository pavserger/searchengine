package main.utils;

import main.model.*;

import java.util.*;

public class RankCalcSort {
    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;
    private IndexRepository indexRepository;


    public RankCalcSort(SiteRepository siteRepository,
                  PageRepository pageRepository,
                  LemmaRepository lemmaRepository,
                  IndexRepository indexRepository) {

        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;

    }




    private List<Integer> numPageList = new ArrayList<>();  // number page for work
    SortedMap <Integer,Integer> sortPageList = new TreeMap<>();
    public List<Integer> getNumPageList() {
        return numPageList;
    }

    public void setNumPageList(List<Integer> numPageList) {


        this.numPageList = numPageList;
    }


    ArrayList <Rank> rankPageList = new ArrayList<>();
    private int numPage;

    public RankCalcSort(List<Integer> indexList) {
      //  this.numPageList = indexList;

       // fillingRankPageList ();

    }

    public void fillingRankPageList() {

      //  sortRank.

        int iSum;
        int ii;
     //   ii = numPageList.indexOf(0);
       for ( ii  = 0; ii < numPageList.size(); ii++ ){
          //  int i2 = ii;
            int i = numPageList.get(ii);
            float f = indexRepository.SumPage(i);
            System.out.println("summa = "+f);
            iSum = (int) f;
            sortPageList.put(iSum,i);
        }
        System.out.println("fillingRankPageList");
        System.out.println("Size " + sortPageList.size());
        System.out.println("Max lemmas in page " + sortPageList.get(sortPageList.size()-1));


    }

}
