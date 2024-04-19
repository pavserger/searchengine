package main.utils;

import main.model.*;

import java.util.*;

public class RankCalcSort {
    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;
    private IndexRepository indexRepository;

    private SortedMap <Integer,Rank> sortRank;

    public RankCalcSort(SiteRepository siteRepository,
                  PageRepository pageRepository,
                  LemmaRepository lemmaRepository,
                  IndexRepository indexRepository) {

        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;

    }




    private List<Integer> numPageList;

    public List<Integer> getNumPageList() {
        return numPageList;
    }

    public void setNumPageList(List<Integer> numPageList) {
        this.numPageList = numPageList;
    }


    ArrayList <Rank> rankPageList = new ArrayList<>();
    private int numPage;

    public RankCalcSort(List<Integer> indexList) {
        this.numPageList = indexList;

        fillingRankPageList ();

    }

    public void fillingRankPageList() {
        List <Index> listIndLemmas;
        float l = indexRepository.SumPage(35);
        System.out.println("summa = "+l);

        sortRank.

/*
       for (Integer  numPage : numPageList);{
            int i = numPage;
            listIndLemmas = dataProcessing.getListIndexInPage(i);
        }

*/

    }

}
