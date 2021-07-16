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
`image` LONGBLOB NOT NULL,
`singer` varchar(45) NOT NULL,
`release_date` date NOT NULL,
`musical_genre` varchar(45) NOT NULL,
`file` LONGBLOB NOT NULL,  
`creator` int(11) NOT NULL,    
PRIMARY KEY (`id`),  
CONSTRAINT `id_user` FOREIGN KEY (`creator`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE);


CREATE TABLE `association` 
(  
`songid` int(11) NOT NULL,
`playlistid` int(11) NOT NULL,
`order` int(11),
PRIMARY KEY (`songid`, `playlistid`),  
CONSTRAINT `id_song` FOREIGN KEY (`songid`) REFERENCES `song` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `id_playlist` FOREIGN KEY (`playlistid`) REFERENCES `playlist` (`id`) ON DELETE CASCADE ON UPDATE CASCADE)
