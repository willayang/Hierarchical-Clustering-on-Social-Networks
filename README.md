# Hierarchical-Clustering-on-Social-Networks
Define the betweenness of an edge (a, b) to be the number of pairs of nodes
x and y such that the edge (a, b) lies on the shortest path between x and y.
To be more precise, since there can be several shortest paths between x and y,
edge (a, b) is credited with the fraction of those shortest paths that include the
edge (a, b). As in golf, a high score is bad. It suggests that the edge (a, b) runs
between two different communities; that is, a and b do not belong to the same
community.

Leskovec, J., Rajaraman, A., & Ullman, J. D. (2014). Mining of massive datasets. Cambridge university press.
Input Example:
Alice, Bruce
David, Ellen
Cindy, Alice
Bruce, Cindy
David, Gary
Frank, David
Ellen, Frank
Bruce, David
Frank, Gary
Output Example:
1 cluster: (Alice, Bruce, Cindy, David, Ellen, Gary, Frank)
2 clusters: (Alice, Bruce, Cindy), (David, Ellen, Gary, Frank)
3 clusters: (Alice, Cindy), (Bruce), (David, Ellen, Gary, Frank)
4 clusters: (Alice, Cindy), (Bruce), (David), (Ellen, Gary, Frank)
6 clusters: (Alice, Cindy), (Bruce), (David), (Ellen), (Gary), (Frank)
7 clusters: (Alice), (Cindy), (Bruce), (David), (Ellen), (Gary), (Frank)
