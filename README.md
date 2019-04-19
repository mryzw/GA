# GA
Generic-Algorithm

We are working on a Vehicle Rounting Problem using Generic Algorithm.

The VRP concerns the service of a delivery company. How things are delivered from one or more depots which has a given set of home vehicles and operated by a set of drivers who can move on a given road network to a set of customers. It asks for a determination of a set of routes, S, (one route for each vehicle that must start and finish at its own depot) such that all customers' requirements and operational constraints are satisfied and the global transportation cost is minimized. This cost here is the distance.

The road network can be described using a graph where the arcs are roads and vertices are junctions between them. The arcs are undirected. Each arc has an associated cost which is generally its length.

The objective function of a VRP can be very different depending on the particular application of the result but a few of the more common objectives are:

- Minimize the global transportation cost based on the global distance travelled as well as the fixed costs associated with the used vehicles and drivers
- Minimize the number of vehicles needed to serve all customers
- Least variation in travel time and vehicle load
- Minimize penalties for low quality service