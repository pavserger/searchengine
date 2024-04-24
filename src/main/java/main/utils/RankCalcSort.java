package main.utils;

import lombok.Data;
import main.model.*;

import java.util.*;

@Data

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
   // private SortedMap <Integer,Integer> sortPageList = new TreeMap<>();
     private List <Rank> sortRank = new ArrayList<>();

    public List<Rank> getSortRank() {
        return sortRank;
    }

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
            Rank rank = new Rank(i,iSum,0.7);
            sortRank.add(rank);
        }

        Collections.sort(sortRank);
     //   System.out.println("fillingRankPageList");
     //   System.out.println("Size " + sortRank.size());
     //   System.out.println("Max lemmas in page " + sortRank.get(sortRank.size()-1));

        int maxPageLemmas = sortRank.get(sortRank.size()-1).absRange;

        for ( int i  = 0; i < sortRank.size(); i++ ){
            double d = sortRank.get(i).absRange/maxPageLemmas;
            sortRank.get(i).setRelRange(d);
        }

        for ( int i  = 0; i < sortRank.size(); i++ ){
            System.out.println (" sort "+ sortRank.get(i));
        }



        }

}
