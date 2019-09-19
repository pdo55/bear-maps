# bear-maps

![img](https://github.com/phuocdo1998/bear-maps/blob/master/data/proj3_imgs/d0_x0_y0.png)

This is the project of implementing the backend of a map web services. The project is included in the Data Structure course of Berkeley

The map uses real-world data downloaded from [BBBike](https://extract.bbbike.org/) so it has several real-world features:

- Extract data and build a graph from OSM file
- Implemented zoom, map rendering, as well as text auto-complete using trie data structure
- Used Djikstra algorithm with A* search enhancement as the core of the route searching feature

The project can also be used with other OSM files from BBBike with some constant modification (they also have the dataset for the entire globe!)

I sincerely hope readers would use this educational project as a tool to learn the intuition of Dijsktra algorithm and other data strucutres, as well as
have a taste of how to handle (read & clean) real-world messy datasets.
