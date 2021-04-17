CREATE TABLE `user` (  
`id` int(11) NOT NULL AUTO_INCREMENT,  
`username` varchar(45) NOT NULL,  `password` varchar(45) NOT NULL,  `name` varchar(45) NOT NULL,  `surname` varchar(45) NOT NULL,  PRIMARY KEY (`id`));

CREATE TABLE `playlist` (  
`id` int(11) NOT NULL AUTO_INCREMENT,  `title` varchar(45)  NOT NULL,  
`creation_date` date NOT NULL,
`creator` int(11) NOT NULL,  
PRIMARY KEY (`id`),  
CONSTRAINT `id_creator` FOREIGN KEY (`creator`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE `song` 
(  
`id` int(11) NOT NULL AUTO_INCREMENT,
`title` varchar(45) NOT NULL,
`image` varbinary(8000)NOT NULL,
`singer` varchar(45) NOT NULL,
`release date` date NOT NULL,
`musical genre` varchar(45) NOT NULL,
`file` varbinary(8000)NOT NULL,  
`creator` int(11) NOT NULL,
`playlist` int(11) NOT NULL,    
PRIMARY KEY (`id`),  
CONSTRAINT `id_user` FOREIGN KEY (`creator`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `id_playlist` FOREIGN KEY (`playlist`) REFERENCES `playlist` (`id`) ON DELETE CASCADE ON UPDATE CASCADE)


